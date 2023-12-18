package io.camunda.connector.generator.postman.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.camunda.connector.generator.postman.utils.ObjectMapperProvider;

public record PostmanCollection(Info info, Item[] item, Object[] variable, Object auth, Object[] event) {
  
  public record Info(String name, String description, Object version){}
  
  public record Item(String id, String name, JsonNode request, Object response, Item[] item) {
    boolean isFolder() {
      return item != null && item.length != 0;
    }
    
    boolean isComplexRequest() {
      return request.isObject();
    }
    
    Request parseRequest() {
      return ObjectMapperProvider.getInstance().convertValue(request, Request.class);
    }
  }
  
  public record Request(JsonNode url, Method method, Header[] header, Body body) {
    boolean isComplexUrl() {
      return url.isObject();
    }
    
    Url parseUrl() {
      return ObjectMapperProvider.getInstance().convertValue(url, Url.class);
    }
  }
  
  public record Url(String raw, QueryParam[] query){}
  
  public record QueryParam(String key, String value){}
  
  public enum Method {
    GET, PUT, POST, PATCH, DELETE, COPY, HEAD, OPTIONS, LINK, UNLINK, PURGE, LOCK, UNLOCK, PROPFIND, VIEW
  }
  
  public record Header(String key, String value){}
  
  public record Body(BodyMode mode, String raw, Object graphql, Object urlencoded, Object formdata){}
  
  public enum BodyMode {
    raw, urlencoded, formdata, file, graphql
  }

}
