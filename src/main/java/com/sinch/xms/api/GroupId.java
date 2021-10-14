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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.immutables.value.Value;

/**
 * Representation of a group identifier. These are in the JSON representation simply string literals
 * but they are within the Java API represented in a more type safe way using this class.
 *
 * <p>To fetch the literal batch identifier use the {@link #toString()} method.
 */
@Value.Immutable
@ValueStylePackageDirect
@ParametersAreNonnullByDefault
public abstract class GroupId implements Comparable<GroupId> {

  /**
   * Builds an immutable {@link GroupId} having the given literal representation.
   *
   * @param id the group id
   * @return a non-null group ID
   */
  @JsonCreator
  @Nonnull
  public static GroupId of(String id) {
    return GroupIdImpl.of(id);
  }

  /**
   * The literal string representation of this group identifier.
   *
   * @return a non-null identifier
   */
  @JsonValue
  protected abstract String id();

  @Override
  public int compareTo(GroupId o) {
    return id().compareTo(o.id());
  }

  /**
   * The string representation of this group identifier.
   *
   * @return a non-null string
   */
  @Override
  public String toString() {
    return id();
  }
}
