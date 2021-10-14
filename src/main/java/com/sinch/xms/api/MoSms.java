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
import javax.annotation.Nullable;
import org.threeten.bp.OffsetDateTime;

/**
 * Base class for mobile originated messages. A mobile originated message can have either a {@link
 * MoTextSms textual} or a {@link MoBinarySms binary} message payload.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(MoTextSms.class), @Type(MoBinarySms.class)})
public abstract class MoSms {

  MoSms() {
    // Intentionally left empty.
  }

  /**
   * The unique message identifier.
   *
   * @return a message identifier
   */
  public abstract String id();

  /**
   * The originating MSISDN.
   *
   * @return an originating address
   */
  @JsonProperty("from")
  public abstract String sender();

  /**
   * The recipient of this mobile originated message. For example an MSISDN or short code.
   *
   * @return the recipient address
   */
  @JsonProperty("to")
  public abstract String recipient();

  /**
   * The MCCMNC of the originating operator, if available.
   *
   * @return an MCCMNC or <code>null</code> if none is available
   */
  @Nullable
  public abstract String operator();

  /**
   * Timestamp for when this message was sent.
   *
   * @return the time of sending
   */
  @JsonProperty("sent_at")
  @Nullable
  public abstract OffsetDateTime sentAt();

  /**
   * Timestamp for when the messaging system received the message.
   *
   * @return the time of receiving
   */
  @JsonProperty("received_at")
  public abstract OffsetDateTime receivedAt();
}
