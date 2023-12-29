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

import io.camunda.connector.generator.postman.model.PostmanCollectionV210;
import io.camunda.connector.generator.postman.utils.ObjectMapperProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record PostmanCollectionsGenerationSource(
    PostmanCollectionV210 collection, Set<String> includeOperations) {

  public PostmanCollectionsGenerationSource(List<String> cliParams) {
    this(fetchPostmanCollection(cliParams), extractOperationIds(cliParams));
  }

  private static PostmanCollectionV210 fetchPostmanCollection(List<String> cliParams) {
    try {
      return ObjectMapperProvider.getInstance()
          .readValue(
              new FileInputStream(
                  "/Users/igpetrov/Workspaces/Connectors/connectors/element-template-generator/postman-collections-parser/src/main/resources/docusign.json"),
              PostmanCollectionV210.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Set<String> extractOperationIds(List<String> cliParams) {
    return Collections.emptySet();
  }
}
