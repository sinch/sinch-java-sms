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
package com.sinch.xms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sinch.xms.api.BatchId;
import com.sinch.xms.api.DeliveryStatus;
import com.sinch.xms.api.GroupId;
import com.sinch.xms.api.ReportType;

/** A Jackson object mapper suitable for use with the Sinch REST API objects. */
public class ApiObjectMapper extends ObjectMapper {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an object mapper suitable for the Sinch REST API. This mapper will serialize pretty
   * printed JSON.
   */
  public ApiObjectMapper() {
    this(true);
  }

  /**
   * Creates an object mapper suitable for the Sinch REST API.
   *
   * @param prettyPrint whether serialized JSON should be pretty printed
   */
  public ApiObjectMapper(boolean prettyPrint) {
    registerModule(new JavaTimeModule());
    setSerializationInclusion(Include.NON_NULL);
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

    /*
     * From jackson-datatype-threetenbp README.md:
     *
     * "Most JSR-310 types are serialized as numbers (integers or decimals as
     * appropriate) if the SerializationFeature#WRITE_DATES_AS_TIMESTAMPS feature is
     * enabled, and otherwise are serialized in standard ISO-8601 string
     * representation."
     */
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

    configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);

    /*
     * Since jackson databind version 2.12 the @JsonValue is no longer ignored in inclusion settings
     * https://github.com/FasterXML/jackson-databind/issues/2909
     * to restore previous behaviour config override is needed
     */
    configOverride(BatchId.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));
    configOverride(ReportType.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));
    configOverride(DeliveryStatus.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));
    configOverride(UpdateValue.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));
    configOverride(GroupId.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));
  }
}
