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
import com.sinch.xms.UpdateValue;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.immutables.value.Value;

/** Objects of this type can be used to update previously submitted MT batches. */
public abstract class MtBatchUpdate {

  /**
   * The message destinations to add to the batch.
   *
   * @return a list of MSISDNs or group IDs
   */
  @Nullable
  @JsonProperty("to_add")
  public abstract List<String> recipientInsertions();

  /**
   * The message destinations to remove from the batch.
   *
   * @return a list of MSISDNs or group IDs
   */
  @Nullable
  @JsonProperty("to_remove")
  public abstract List<String> recipientRemovals();

  /**
   * The message originator.
   *
   * @return an MSISDN or short code
   */
  @Nullable
  @JsonProperty("from")
  public abstract String sender();

  /**
   * Description of how to update the batch delivery report value.
   *
   * @return an update description
   * @see MtBatchCreate#deliveryReport()
   */
  @Nullable
  @JsonProperty("delivery_report")
  public abstract UpdateValue<ReportType> deliveryReport();

  /**
   * Description of how to update the batch send at value.
   *
   * @return an update description
   * @see MtBatchCreate#sendAt()
   */
  @Nullable
  @JsonProperty("send_at")
  public abstract UpdateValue<OffsetDateTime> sendAt();

  /**
   * Description of how to update the batch expire at value.
   *
   * @return an update description
   * @see MtBatchCreate#expireAt()
   */
  @Nullable
  @JsonProperty("expire_at")
  public abstract UpdateValue<OffsetDateTime> expireAt();

  /**
   * Description of how to update the batch callback URL.
   *
   * @return an update description
   * @see MtBatchCreate#callbackUrl()
   */
  @Nullable
  @JsonProperty("callback_url")
  public abstract UpdateValue<URI> callbackUrl();

  /** Validates that this object is in a correct state. */
  @OverridingMethodsMustInvokeSuper
  @Value.Check
  protected void check() {
    if (sender() != null && sender().isEmpty()) {
      throw new IllegalStateException("empty from address");
    }
  }
}
