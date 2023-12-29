/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.camunda.connector.generator.postman.utils;

import io.camunda.connector.generator.dsl.http.HttpFeelBuilder;
import io.camunda.connector.generator.dsl.http.HttpOperation;
import io.camunda.connector.generator.dsl.http.HttpOperationProperty;
import io.camunda.connector.generator.postman.OperationParseResult;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item.Endpoint;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item.Endpoint.Request.Url;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item.Folder;
import io.camunda.connector.generator.postman.utils.PostmanBodyUtil.BodyParseResult;
import io.camunda.connector.http.base.model.HttpMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

public class PostmanOperationUtil {

  // TODO: return options
  public static List<OperationParseResult> extractOperations(
      PostmanCollectionV210 collection, Set<String> includeOperations, List<String> variables) {
    final var operations = new ArrayList<OperationParseResult>();
    final var allOperationsRegistry = traverseCollection(collection);
    final var includedRequests = new ArrayList<Item>();
    for (String includedOp : includeOperations) {
      includedRequests.add(allOperationsRegistry.get(includedOp.toUpperCase()));
    }
    for (Item includedRequest : includedRequests) {
      var endpoint = (Endpoint) includedRequest;
      OperationParseResult res = extractOperation(endpoint, variables);
      res.builder().pathFeelExpression(extractPath(res.path()));
      operations.add(res);
    }
    return operations;
  }
  
  private static Map<String, Item> traverseCollection(PostmanCollectionV210 collection) {
    var operations = new HashMap<String, Item>();
    for (Item item : collection.items()) {
      operations.putAll(traverseCollectionNode(item, StringUtils.EMPTY));
    }
    return operations;
  }
  
  private static Map<String, Item> traverseCollectionNode(Item node, String prefix) {
    var operations = new HashMap<String, Item>();
    
    if (node instanceof Folder folder) {
      for (Item item : folder.items()) {
        operations.putAll(traverseCollectionNode(item, folder.name()));
      }
    } else if (node instanceof Endpoint endpoint) {
      operations.put(prefix.toUpperCase() + "/" + endpoint.name().toUpperCase(), node);
    } else {
      throw new RuntimeException("Item type not supported");
    }
    
    return operations;
  }
  
  private static String extractPathFromUrl(Url originalUrl, List<String> variables) {
    String path = originalUrl.raw();
    Map<String, String> splittedVariables = variables.stream().map(v -> v.split("=", 2)).collect(Collectors.toMap(s -> s[0], s -> s[1]));
    StrSubstitutor variableSubstitutor = new StrSubstitutor(splittedVariables, "{{", "}}");
    return variableSubstitutor.replace(path).replace("{{", "{").replace("}}", "}");
  }

  private static HttpFeelBuilder extractPath(String rawPath) {
    // split path into parts, each part is either a variable or a constant
    String[] pathParts = rawPath.split("\\{");
    var builder = HttpFeelBuilder.string();
    if (pathParts.length == 1) {
      // no variables
      builder.part(rawPath);
    } else {
      for (String pathPart : pathParts) {
        if (pathPart.contains("}")) {
          String[] variableParts = pathPart.split("}");
          // replace dashes in variable names with underscores, same must be done for properties
          var property = variableParts[0].replace("-", "_");
          builder.property(property);
          if (variableParts.length > 1) {
            builder.part(variableParts[1]);
          }
        } else {
          builder.part(pathPart);
        }
      }
    }
    return builder;
  }
  
  private static OperationParseResult extractOperation(Endpoint endpoint, List<String> variables) {
    Set<HttpOperationProperty> requestConfigurationProps = new HashSet<>();

    var label = endpoint.name() + " " + endpoint.request().url().raw();
    var operationId = label.toLowerCase().trim().replace(" ", "_");
    
    var queryParams = endpoint.request().url().queryParam();
    if (queryParams != null && !queryParams.isEmpty()) {
      // TODO: add query
    }
    
    // TODO: headers
    
    // TODO: authentication override
    
    var body = PostmanBodyUtil.parseBody(endpoint);
    HttpFeelBuilder bodyFeelExpression = null;
    if (body instanceof BodyParseResult.Raw rawBody) {
      bodyFeelExpression = HttpFeelBuilder.preFormatted("=" + rawBody.rawBody());
    } else if (body instanceof BodyParseResult.Detailed detailedBody) {
      bodyFeelExpression = detailedBody.feelBuilder();
      requestConfigurationProps.addAll(detailedBody.properties());
    }
    
    var opBuilder = HttpOperation.builder()
        .id(operationId)
        .label(label)
        .bodyFeelExpression(bodyFeelExpression)
        // TODO: auth override
        .method(HttpMethod.valueOf(endpoint.request().method().name()))
        .properties(requestConfigurationProps);
    
    return new OperationParseResult(operationId, extractPathFromUrl(endpoint.request().url(), variables), true, null, opBuilder);
  }
}
