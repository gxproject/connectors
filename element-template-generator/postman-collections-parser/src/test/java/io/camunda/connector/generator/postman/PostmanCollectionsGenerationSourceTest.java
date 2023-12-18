package io.camunda.connector.generator.postman;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PostmanCollectionsGenerationSourceTest {
  
  @Test
  void testNew() {
    var source = new PostmanCollectionsGenerationSource(new ArrayList<>());
    Assertions.assertThat(source).isNotNull();
  }

}