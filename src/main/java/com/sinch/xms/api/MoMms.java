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
import org.immutables.value.Value;

/** A mobile originated MMS message. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MoMmsImpl.class)
@JsonTypeName("mo_media")
public abstract class MoMms extends MoSms {

  /** A builder of mms MO messages. */
  public static final class Builder extends MoMmsImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MoMms} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MoMms.Builder builder() {
    return new Builder();
  }

  /**
   * The MMS message body.
   *
   * @return an object containing the body
   */
  @Nonnull
  public abstract MoMmsBody body();
}
