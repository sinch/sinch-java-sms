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
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Objects of this class contain information about a textual MMS batch. The information includes the
 * message body, the batch identifier, the creation time, and so on.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchMmsResult.Builder.class)
@JsonTypeName("mt_media")
public abstract class MtBatchMmsResult extends MtBatchResult {

  /** Builder of MMS batch results. */
  public static class Builder extends MtBatchMmsResultImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MtBatchMmsResult} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MtBatchMmsResult.Builder builder() {
    return new Builder();
  }

  /**
   * The message body including message text or template (optional) and the media content url. If
   * this describes a template then {@link #parameters()} describes the parameter substitutions.
   *
   * <p>See {@link MtBatchMmsCreate#body()} for a more thorough description of this field.
   *
   * @return the message to send
   */
  public abstract MediaBody body();

  /**
   * Whether the media included in the message to be checked against Sinch MMS channel best
   * practices. If set to true, the message will be rejected if it doesn't conform to the listed
   * recommendations, otherwise no validation will be performed. Defaults to false.
   *
   * @return boolean indicating if strict validation is meant to be performed
   */
  @Nullable
  @JsonProperty("strict_validation")
  public abstract Boolean strictValidation();

  /**
   * The message template parameter substitutions. If {@link #body()} describes a template then this
   * returns the substitutions for the template parameters.
   *
   * @return a map from template variable to parameter values
   * @see #body()
   */
  @JsonInclude(Include.NON_EMPTY)
  public abstract Map<String, ParameterValues> parameters();
}
