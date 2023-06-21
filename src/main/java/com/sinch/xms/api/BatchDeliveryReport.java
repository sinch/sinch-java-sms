/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/** A batch delivery report. */
@Value.Enclosing
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(BatchDeliveryReportSms.class), @Type(BatchDeliveryReportMms.class)})
public abstract class BatchDeliveryReport {

  /**
   * Identifier of the batch to which this delivery report refers.
   *
   * @return a non-null batch identifier
   */
  @JsonProperty("batch_id")
  public abstract BatchId batchId();

  /**
   * The total number of messages in the batch. This is including message expansion, that is,
   * including messages needing multiple parts.
   *
   * @return a positive integer
   */
  @JsonProperty("total_message_count")
  public abstract int totalMessageCount();

  /**
   * A list of {@link Status statuses} for the batch. Only non-empty statuses are present here, that
   * is, for each member status there is at least one message having the state.
   *
   * @return a list of statuses
   */
  public abstract List<Status> statuses();

  /**
   * The optional client identifier attached to this message.
   *
   * @return a client reference id
   */
  @Nullable
  @JsonProperty("client_reference")
  public abstract String clientReference();
}
