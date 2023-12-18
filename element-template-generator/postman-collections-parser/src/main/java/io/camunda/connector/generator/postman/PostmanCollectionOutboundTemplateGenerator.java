package io.camunda.connector.generator.postman;

import io.camunda.connector.generator.api.CliCompatibleTemplateGenerator;
import io.camunda.connector.generator.api.GeneratorConfiguration;
import io.camunda.connector.generator.dsl.OutboundElementTemplate;
import java.util.List;

public class PostmanCollectionOutboundTemplateGenerator implements
    CliCompatibleTemplateGenerator<PostmanCollectionsGenerationSource, OutboundElementTemplate> {

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
  public List<OutboundElementTemplate> generate(PostmanCollectionsGenerationSource source,
      GeneratorConfiguration configuration) {
    return null;
  }
}
