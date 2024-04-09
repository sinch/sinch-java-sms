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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/** A mobile originated MMS message. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MoMmsMediaImpl.class)
@JsonTypeName("mo_media")
public abstract class MoMmsMedia {

  /** A builder of mms MO messages. */
  public static final class Builder extends MoMmsMediaImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MoMmsMedia} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MoMmsMedia.Builder builder() {
    return new Builder();
  }

  /**
   * URL to be used to download attachment.
   *
   * @return the media url
   */
  @Nullable
  public abstract String url();
  /**
   * The content type of provided media. For example, 'image/jpeg'
   *
   * @return the content type
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">Content types</a>
   */
  @Nonnull
  public abstract String contentType();
  /**
   * The status received while media upload to storage. Possible values are: Uploaded or Failed.
   *
   * @return the media status
   */
  @Nonnull
  public abstract MediaStatus status();
  /**
   * Error code in case of failure or 0 for success.
   *
   * @return the status code
   */
  @Nonnull
  public abstract int code();

  public enum MediaStatus {
    Uploaded("Uploaded"),
    Failed("Failed");

    private final String publicName;

    MediaStatus(String publicName) {
      this.publicName = publicName;
    }

    @Override
    public String toString() {
      return publicName;
    }
  }
}
