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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/** A batch delivery report. */
@Value.Enclosing
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = BatchDeliveryReportSms.Builder.class)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("delivery_report_sms")
public abstract class BatchDeliveryReportSms extends BatchDeliveryReport {

  /** A builder of batch delivery reports. */
  public static class Builder extends BatchDeliveryReportSmsImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link BatchDeliveryReportSms} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final BatchDeliveryReportSms.Builder builder() {
    return new Builder();
  }
}
