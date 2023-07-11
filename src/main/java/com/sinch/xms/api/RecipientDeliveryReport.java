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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.time.OffsetDateTime;
import javax.annotation.Nullable;

/** Representation of a delivery report for a specific recipient. */
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(RecipientDeliveryReportSms.class), @Type(RecipientDeliveryReportMms.class)})
public abstract class RecipientDeliveryReport {

  /**
   * The batch to which this delivery report belongs
   *
   * @return a batch identifier
   */
  @JsonProperty("batch_id")
  public abstract BatchId batchId();

  /**
   * The recipient to which this delivery report refers.
   *
   * @return an MSISDN
   */
  public abstract String recipient();

  /**
   * The delivery report error code of the message.
   *
   * @return a delivery report error code
   */
  public abstract int code();

  /**
   * The delivery status of the message.
   *
   * @return a delivery status
   */
  public abstract DeliveryStatus status();

  /**
   * A description of the status, if available.
   *
   * @return a status description
   */
  @JsonProperty("status_message")
  @Nullable
  public abstract String statusMessage();

  /**
   * The operator MCCMNC, if available.
   *
   * @return an operator identifier; <code>null</code> if unknown
   */
  @Nullable
  public abstract String operator();

  /**
   * Time when the message reached it's final state.
   *
   * @return a date and time
   */
  public abstract OffsetDateTime at();

  /**
   * The message timestamp as recorded by the network operator, if message dispatched.
   *
   * @return a date and time if message dispatched; <code>null</code> otherwise
   */
  @JsonProperty("operator_status_at")
  @Nullable
  public abstract OffsetDateTime operatorStatusAt();

  /**
   * The optional client identifier attached to this message.
   *
   * @return a client reference id
   */
  @Nullable
  @JsonProperty("client_reference")
  public abstract String clientReference();

  @Nullable
  @JsonProperty("encoding")
  public abstract String encoding();
}
