package io.camunda.connector.generator.postman.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperProvider {
  
  private static final ObjectMapper INSTANCE = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
  public static ObjectMapper getInstance() {
    return INSTANCE;
  }

}
