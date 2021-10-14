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

/** A page within a paged group listing. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedGroupResult.Builder.class)
public abstract class PagedGroupResult extends Page<GroupResult> {

  /** A builder of group result pages. */
  public static class Builder extends PagedGroupResultImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link PagedGroupResult} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final PagedGroupResult.Builder builder() {
    return new Builder();
  }

  @JsonProperty("groups")
  @Override
  public abstract List<GroupResult> content();
}
