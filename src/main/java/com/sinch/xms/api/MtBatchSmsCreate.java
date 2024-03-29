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
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.immutables.value.Value;

/**
 * Base class for mobile terminated SMS batch messages. A mobile terminated message can have either
 * a {@link MtBatchTextSmsCreate textual} or a {@link MtBatchBinarySmsCreate binary} message
 * payload.
 */
@JsonSubTypes({@Type(MtBatchTextSmsCreate.class), @Type(MtBatchBinarySmsCreate.class)})
public abstract class MtBatchSmsCreate extends MtBatchCreate {

  /**
   * Shows message on screen without user interaction while not saving the message to the inbox.
   * Defaults to false.
   *
   * @return boolean indicating if it's a flash message
   */
  @Nullable
  @JsonProperty("flash_message")
  public abstract Boolean flashMessage();

  /**
   * Message will be dispatched only if it is not split to more parts than Max Number of Message
   * Parts. Defaults to false.
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

  @OverridingMethodsMustInvokeSuper
  @Value.Check
  protected void check() {
    if (recipients().isEmpty()) {
      throw new IllegalStateException("no destination");
    }

    for (String to : recipients()) {
      if (to.isEmpty()) {
        throw new IllegalStateException("contains empty destination");
      }
    }

    if (sender() != null && sender().isEmpty()) {
      throw new IllegalStateException("empty from address");
    }
  }
}
