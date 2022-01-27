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
import java.net.URI;
import java.util.List;
import javax.annotation.Nullable;
import org.threeten.bp.OffsetDateTime;

/**
 * Base class for batch description classes. This contains the fields common for both {@link
 * MtBatchTextSmsResult textual} and {@link MtBatchBinarySmsResult binary} batches.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(MtBatchTextSmsResult.class), @Type(MtBatchBinarySmsResult.class)})
public abstract class MtBatchSmsResult {

  MtBatchSmsResult() {
    // Intentionally left empty.
  }

  /**
   * The unique batch identifier. This identifier can be used to, for example fetch a delivery
   * reports and update or cancel the batch.
   *
   * @return a batch identifier
   */
  public abstract BatchId id();

  /**
   * The list of message recipients. May not be empty.
   *
   * @return a non-empty list of recipients
   */
  @JsonProperty("to")
  public abstract List<String> recipients();

  /**
   * The message originator. May be an MSISDN or short code.
   *
   * @return an originator address
   */
  @JsonProperty("from")
  @Nullable
  public abstract String sender();

  /**
   * The type of delivery report used for this batch.
   *
   * @return a type of report, <code>ReportType.NONE</code> if not provided
   */
  public abstract ReportType deliveryReport();

  /**
   * The URL to which batch callbacks are sent. If <code>null</code> then callbacks will be sent to
   * the default URL.
   *
   * @return an URL or <code>null</code> if the default callback URL is used
   */
  @Nullable
  public abstract URI callbackUrl();

  /**
   * The scheduled time this batch will be sent. If <code>null</code> or set to a past time then the
   * batch is sent immediately.
   *
   * @return the time when this batch will be sent
   */
  @Nullable
  @JsonProperty("send_at")
  public abstract OffsetDateTime sendAt();

  /**
   * The time when this batch will expire. Any message not delivered by this time will be placed
   * into an expired state and no further delivery will be attempted.
   *
   * @return the time when this batch expires
   */
  @Nullable
  @JsonProperty("expire_at")
  public abstract OffsetDateTime expireAt();

  /**
   * The time when this batch was created.
   *
   * @return the time when this batch was created
   */
  @Nullable
  @JsonProperty("created_at")
  public abstract OffsetDateTime createdAt();

  /**
   * The time when this batch was last modified.
   *
   * @return the time when this batch was last modified
   */
  @Nullable
  @JsonProperty("modified_at")
  public abstract OffsetDateTime modifiedAt();

  /**
   * Whether this batch has been canceled.
   *
   * @return <code>true</code> if the batch is canceled; <code>false</code> otherwise
   */
  public abstract boolean canceled();

  /**
   * The client identifier to attach to this message. If set, it will be added in the delivery
   * report/callback of this batch.
   *
   * @return a client reference id
   */
  @Nullable
  @JsonProperty("client_reference")
  public abstract String clientReference();

  /**
   * Shows message on screen without user interaction while not saving the message to the inbox.
   *
   * @return boolean indicating if it's a flash message
   */
  @JsonProperty("flash_message")
  public abstract boolean flashMessage();

  /**
   * Message will be dispatched only if it is not split to more parts than Max Number of Message
   * Parts.
   *
   * @return the maximum allowed number of message parts
   */
  @Nullable
  @JsonProperty("max_number_of_message_parts")
  public abstract Integer maxNumberOfMessageParts();

  /**
   * The DLT principal entity identifier to attach to this message.
   *
   * @return a principal entity id
   */
  @Nullable
  @JsonProperty("dlt_principal_entity_id")
  public abstract String dltPrincipalEntity();

  /**
   * The DLT template identifier to attach to this message.
   *
   * @return a template id
   */
  @Nullable
  @JsonProperty("dlt_template_id")
  public abstract String dltTemplateId();

  /**
   * The type of number of the message originator. Valid values are 0 to 6. This is optional and
   * used for overriding the automatic detection of type of number. If provided then from_npi must
   * also be set.
   *
   * @return the type of number for the originator address
   */
  @JsonProperty("from_ton")
  @Nullable
  public abstract Integer senderTon();

  /**
   * The numbering plan identification of the message originator. Valid values are 0 to 18. This is
   * optional and used for overriding the automatic detection. If provided then from_ton must also
   * be set.
   *
   * @return the numbering plan identification for the originator address
   */
  @JsonProperty("from_npi")
  @Nullable
  public abstract Integer senderNpi();
}
