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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/** Representation of an SMS delivery report for a specific recipient. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = RecipientDeliveryReportSms.Builder.class)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("recipient_delivery_report_sms")
public abstract class RecipientDeliveryReportSms extends RecipientDeliveryReport {

  /** A builder of recipient delivery reports. */
  public static final class Builder extends RecipientDeliveryReportSmsImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link RecipientDeliveryReportSms} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final RecipientDeliveryReportSms.Builder builder() {
    return new Builder();
  }
}
