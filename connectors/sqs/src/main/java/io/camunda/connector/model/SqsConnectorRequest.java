/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. Licensed under a proprietary license.
 * See the License.txt file for more information. You may not use this file
 * except in compliance with the proprietary license.
 */
package io.camunda.connector.model;

import io.camunda.connector.api.annotation.Secret;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SqsConnectorRequest {

  @Valid @NotNull @Secret private SqsAuthenticationRequestData authentication;
  @Valid @NotNull @Secret private QueueRequestData queue;

  public SqsAuthenticationRequestData getAuthentication() {
    return authentication;
  }

  public void setAuthentication(final SqsAuthenticationRequestData authentication) {
    this.authentication = authentication;
  }

  public QueueRequestData getQueue() {
    return queue;
  }

  public void setQueue(final QueueRequestData queue) {
    this.queue = queue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SqsConnectorRequest that = (SqsConnectorRequest) o;
    return authentication.equals(that.authentication) && queue.equals(that.queue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authentication, queue);
  }

  @Override
  public String toString() {
    return "SqsConnectorRequest{" + "authentication=" + authentication + ", queue=" + queue + '}';
  }
}
