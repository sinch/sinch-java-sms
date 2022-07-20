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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.immutables.value.Value;

/**
 * Describes the different types of finalized delivery reports supported by XMS.
 *
 * <p>A number of predefined delivery statuses are provided as constants within this class, for
 * example, {@link #DELIVERED} or {@link #FAILED}, but XMS reserves the right to add new codes in
 * the future.
 */
@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class FinalizedDeliveryStatus {

  /** Message was aborted before reaching SMSC. */
  public static final FinalizedDeliveryStatus ABORTED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.ABORTED);

  /** Message was cancelled before reaching SMSC. */
  public static final FinalizedDeliveryStatus CANCELLED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.CANCELLED);

  /** Message was rejected by SMSC. */
  public static final FinalizedDeliveryStatus REJECTED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.REJECTED);

  /** Message has been delivered. */
  public static final FinalizedDeliveryStatus DELIVERED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.DELIVERED);

  /** Message failed to be delivered. */
  public static final FinalizedDeliveryStatus FAILED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.FAILED);

  /** Message expired before delivery. */
  public static final FinalizedDeliveryStatus EXPIRED =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.EXPIRED);

  /** It is not known if message was delivered or not. */
  public static final FinalizedDeliveryStatus UNKNOWN =
      FinalizedDeliveryStatusImpl.of(DeliveryStatus.UNKNOWN);

  /**
   * The string representation of this delivery status.
   *
   * @return a non-null string
   */
  @JsonValue
  public abstract String status();

  /**
   * Creates a delivery status object from the given string representation.
   *
   * @param status string describing the status
   * @return a non-null delivery status object
   */
  @JsonCreator
  public static FinalizedDeliveryStatus of(DeliveryStatus status) {
    return FinalizedDeliveryStatusImpl.of(status.status());
  }
}
