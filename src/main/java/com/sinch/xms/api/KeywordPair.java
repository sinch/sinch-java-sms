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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * A pair containing the first and second word of a group auto update trigger. Either trigger word
 * may be <code>null</code>.
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = KeywordPairImpl.class)
public abstract class KeywordPair {

  /**
   * The first keyword.
   *
   * @return an SMS keyword or <code>null</code> if no keyword is set
   */
  @Nullable
  @JsonProperty("first_word")
  public abstract String firstWord();

  /**
   * The second keyword.
   *
   * @return an SMS keyword or <code>null</code> if no keyword is set
   */
  @Nullable
  @JsonProperty("second_word")
  public abstract String secondWord();

  /**
   * Builder of keyword pairs.
   *
   * @param firstWord the first keyword
   * @param secondWord the second keyword
   * @return a keyword pair consisting of the given keywords
   */
  @Nonnull
  public static KeywordPair of(@Nullable String firstWord, @Nullable String secondWord) {
    return KeywordPairImpl.of(firstWord, secondWord);
  }
}
