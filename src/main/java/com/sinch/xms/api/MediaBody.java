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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Representation of a media body. The text part is optional but the media url is mandatory.
 *
 * <p>Supported media types are:
 *
 * <ul>
 *   <li>image: .jpg, .png (please observe that .jpg files have wider support on mobile devices than
 *       .png files)
 *   <li>video: .mp4, .gif, .mov
 *   <li>vCard (Virtual Contact File): .vcf
 *   <li>PDF files: .pdf
 * </ul>
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MediaBody.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public abstract class MediaBody {

  /** A builder of media body creation descriptions. */
  public static final class Builder extends MediaBodyImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MediaBody} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MediaBody.Builder builder() {
    return new MediaBody.Builder();
  }

  /**
   * The text message.
   *
   * @return a message
   */
  @Nullable
  public abstract String message();

  /**
   * The url to the media content.
   *
   * @return a non-null url to the media content
   */
  @Nonnull
  public abstract String url();
}
