package io.camunda.connector.generator.postman;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.generator.postman.model.PostmanCollection;
import io.camunda.connector.generator.postman.utils.ObjectMapperProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record PostmanCollectionsGenerationSource(PostmanCollection collection, Set<String> includeOperations) {
  
  public PostmanCollectionsGenerationSource(List<String> cliParams) {
    this(fetchPostmanCollection(cliParams), extractOperationIds(cliParams));
  }

  private static PostmanCollection fetchPostmanCollection(List<String> cliParams) {
    try {
      return ObjectMapperProvider.getInstance().readValue(new FileInputStream(
          "/Users/igpetrov/Workspaces/Connectors/connectors/element-template-generator/postman-collections-parser/src/main/resources/docusign.json"), PostmanCollection.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static Set<String> extractOperationIds(List<String> cliParams) {
    return Collections.emptySet();
  }
  
}
