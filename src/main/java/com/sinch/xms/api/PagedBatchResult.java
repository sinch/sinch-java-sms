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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/** A page within a paged batch listing. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedBatchResult.Builder.class)
public abstract class PagedBatchResult extends Page<MtBatchResult> {

  /** A builder of batch result pages. */
  public static class Builder extends PagedBatchResultImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link PagedBatchResult} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final PagedBatchResult.Builder builder() {
    return new Builder();
  }

  @JsonProperty("batches")
  @Override
  public abstract List<MtBatchResult> content();
}
