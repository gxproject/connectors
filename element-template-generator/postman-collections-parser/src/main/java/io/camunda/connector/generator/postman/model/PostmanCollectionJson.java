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
package io.camunda.connector.generator.postman.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.camunda.connector.generator.postman.utils.ObjectMapperProvider;
import java.util.List;

public record PostmanCollectionJson(
    Info info, List<Item> item, List<?> variable, List<?> auth, List<?> event) {

  public record Info(String name, String description, Object version) {}

  public record Item(String id, String name, JsonNode request, Object response, List<Item> item) {
    public boolean isFolder() {
      return item != null && !item.isEmpty();
    }

    public boolean isComplexRequest() {
      return request.isObject();
    }

    public Request parseRequest() {
      return ObjectMapperProvider.getInstance().convertValue(request, Request.class);
    }
  }

  public record Request(JsonNode url, Method method, List<Header> header, Body body) {
    public boolean isComplexUrl() {
      return url.isObject();
    }

    public Url parseUrl() {
      return ObjectMapperProvider.getInstance().convertValue(url, Url.class);
    }

    public enum Method {
      GET,
      PUT,
      POST,
      PATCH,
      DELETE,
      COPY,
      HEAD,
      OPTIONS,
      LINK,
      UNLINK,
      PURGE,
      LOCK,
      UNLOCK,
      PROPFIND,
      VIEW
    }
  }

  public record Url(String raw, List<QueryParam> query) {}

  public record QueryParam(String key, String value) {}

  public record Header(String key, String value) {}

  public record Body(
      BodyMode mode, 
      String raw, 
      Object graphql, 
      Object urlencoded, 
      Object formdata) {
    public enum BodyMode {
      raw,
      urlencoded,
      formdata,
      file,
      graphql
    }
  }
}
