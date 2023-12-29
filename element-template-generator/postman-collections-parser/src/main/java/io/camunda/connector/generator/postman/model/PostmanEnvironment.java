package io.camunda.connector.generator.postman.model;

import java.util.List;

public record PostmanEnvironment(String id, String name, List<Value> values) {
  
  public record Value(String key, String value, boolean enabled){}

}
