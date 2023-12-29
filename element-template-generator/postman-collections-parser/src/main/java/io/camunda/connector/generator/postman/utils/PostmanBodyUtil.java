package io.camunda.connector.generator.postman.utils;

import io.camunda.connector.generator.dsl.http.HttpFeelBuilder;
import io.camunda.connector.generator.dsl.http.HttpOperationProperty;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item.Endpoint;
import io.camunda.connector.generator.postman.model.PostmanCollectionV210.Item.Endpoint.Request.Body.BodyMode;
import io.camunda.connector.generator.postman.utils.PostmanBodyUtil.BodyParseResult.Raw;
import java.util.List;

public class PostmanBodyUtil {

  private static final List<String> SUPPORTED_BODY_MEDIA_TYPES =
      List.of("application/json", "text/plain");

  public sealed interface BodyParseResult permits BodyParseResult.Detailed, BodyParseResult.Raw {

    record Detailed(HttpFeelBuilder feelBuilder, List<HttpOperationProperty> properties)
        implements BodyParseResult {}

    record Raw(String rawBody) implements BodyParseResult {}
  }

  public static BodyParseResult parseBody(Endpoint endpoint) {
    if (endpoint.request().body() == null) {
      return new Raw("");
    }
    
    if (endpoint.request().body().mode() == BodyMode.raw) {
      return new Raw(endpoint.request().body().raw());
    }
    
    
    return null;
  }

}
