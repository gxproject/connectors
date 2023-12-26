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
package io.camunda.connector.generator.postman;

import io.camunda.connector.generator.api.CliCompatibleTemplateGenerator;
import io.camunda.connector.generator.api.GeneratorConfiguration;
import io.camunda.connector.generator.api.GeneratorConfiguration.ConnectorElementType;
import io.camunda.connector.generator.api.GeneratorConfiguration.ConnectorMode;
import io.camunda.connector.generator.dsl.BpmnType;
import io.camunda.connector.generator.dsl.OutboundElementTemplate;
import io.camunda.connector.generator.dsl.http.HttpAuthentication;
import io.camunda.connector.generator.dsl.http.HttpOperationBuilder;
import io.camunda.connector.generator.dsl.http.HttpOutboundElementTemplateBuilder;
import io.camunda.connector.generator.dsl.http.HttpServerData;
import io.camunda.connector.generator.postman.model.PostmanCollection;
import io.camunda.connector.generator.postman.utils.PostmanOperationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostmanCollectionOutboundTemplateGenerator
    implements CliCompatibleTemplateGenerator<
        PostmanCollectionsGenerationSource, OutboundElementTemplate> {

  private static final Logger LOG =
      LoggerFactory.getLogger(PostmanCollectionOutboundTemplateGenerator.class);

  private static final Set<BpmnType> SUPPORTED_ELEMENT_TYPES =
      Set.of(BpmnType.SERVICE_TASK, BpmnType.INTERMEDIATE_THROW_EVENT);
  private static final ConnectorElementType DEFAULT_ELEMENT_TYPE =
      new ConnectorElementType(Set.of(BpmnType.TASK), BpmnType.SERVICE_TASK);

  @Override
  public String getGeneratorId() {
    return "postman-collections-outbound";
  }

  @Override
  public PostmanCollectionsGenerationSource prepareInput(List<String> parameters) {
    return new PostmanCollectionsGenerationSource(parameters);
  }

  @Override
  public String getUsage() {
    return "TBD";
  }

  @Override
  public ScanResult scan(PostmanCollectionsGenerationSource input) {
    return null;
  }

  @Override
  public List<OutboundElementTemplate> generate(
      PostmanCollectionsGenerationSource source, GeneratorConfiguration configuration) {
    // TODO extract operations

    // TODO options
    var operations =
        PostmanOperationUtil.extractOperations(source.collection(), source.includeOperations());
    if (operations.isEmpty()) {
      throw new IllegalArgumentException("No operations found in OpenAPI document");
    }
    var supportedOperations =
        operations.stream()
            .filter(
                op -> {
                  if (op.supported()) {
                    return true;
                  }
                  LOG.warn(
                      "Operation {} is not supported, reason: {}. It will be skipped",
                      op.id(),
                      op.info());
                  return false;
                })
            .map(OperationParseResult::builder)
            .toList();

    return buildTemplates(source.collection(), supportedOperations, configuration);
  }

  private List<OutboundElementTemplate> buildTemplates(
      PostmanCollection postmanCollection,
      List<HttpOperationBuilder> operationBuilders,
      GeneratorConfiguration configuration) {
    if (configuration == null) {
      configuration = GeneratorConfiguration.DEFAULT;
    }

    // TODO: parse authentication

    var elementTypes = configuration.elementTypes();
    if (elementTypes == null) {
      elementTypes = Set.of(DEFAULT_ELEMENT_TYPE);
    }
    elementTypes.stream()
        .filter(t -> !SUPPORTED_ELEMENT_TYPES.contains(t.elementType()))
        .findFirst()
        .ifPresent(
            t -> {
              throw new IllegalArgumentException(
                  String.format("Unsupported element type '%s'", t.elementType().getName()));
            });
    if (elementTypes.isEmpty()) {
      elementTypes = Set.of(DEFAULT_ELEMENT_TYPE);
    }

    List<OutboundElementTemplate> templates = new ArrayList<>();
    for (var elementType : elementTypes) {
      // TODO: authentication
      var template = buildTemplate(postmanCollection, operationBuilders, configuration, null);
      template.elementType(elementType);
      templates.add(template.build());
    }
    return templates;
  }

  private HttpOutboundElementTemplateBuilder buildTemplate(
      PostmanCollection postmanCollection,
      List<HttpOperationBuilder> operationBuilders,
      GeneratorConfiguration configuration,
      List<HttpAuthentication> authentication) {
    var info = postmanCollection.info();
    return HttpOutboundElementTemplateBuilder.create(
            ConnectorMode.HYBRID.equals(configuration.connectorMode()))
        .id(
            configuration.templateId() != null
                ? configuration.templateId()
                : getIdFromApiTitle(info))
        .name(configuration.templateName() != null ? configuration.templateName() : info.name())
        // TODO add proper version handling
        //        .version(
        //            processVersion(
        //                configuration.templateVersion() != null
        //                    ? configuration.templateVersion().toString()
        //                    : info.getVersion()))
        .version(1)
        .operations(
            operationBuilders.stream()
                .map(HttpOperationBuilder::build)
                .collect(Collectors.toList()))
        // TODO authentication
        // .authentication(authentication)
        // TODO proper server implementation
        .servers(extractServers(postmanCollection));
  }

  private String getIdFromApiTitle(PostmanCollection.Info info) {
    return info.name().trim().replace(" ", "-").toLowerCase();
  }

  private List<HttpServerData> extractServers(PostmanCollection collection) {
    return List.of(new HttpServerData("https://httpbin.org", "Label"));
  }
}
