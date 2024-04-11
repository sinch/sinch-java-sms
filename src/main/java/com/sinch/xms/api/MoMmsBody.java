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
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/** A mobile originated MMS message. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MoMmsBodyImpl.class)
@JsonTypeName("mo_media")
public abstract class MoMmsBody {

  /** A builder of mms MO messages. */
  public static final class Builder extends MoMmsBodyImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MoMmsBody} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MoMmsBody.Builder builder() {
    return new Builder();
  }

  /**
   * The message subject, if available.
   *
   * @return the message subject
   */
  @Nullable
  public abstract String subject();
  /**
   * The textual message body, if available.
   *
   * @return the message body or `null` if not available.
   */
  @Nullable
  public abstract String message();
  /**
   * The list of attached media.
   *
   * @return the list of media objects
   */
  @Nonnull
  public abstract List<MoMmsMedia> media();
}
