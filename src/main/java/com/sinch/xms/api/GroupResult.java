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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Objects of this class contain information about a group. The information
 * includes creation time stamp and group identifier and it must therefore be
 * populated by XMS, which is the source of such information.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = GroupResult.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public abstract class GroupResult {

	/**
	 * A builder of group results.
	 */
	public static final class Builder extends GroupResultImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link GroupResult} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final GroupResult.Builder builder() {
		return new Builder();
	}

	/**
	 * The unique group ID that identifies this group.
	 * 
	 * @return a non-null group ID
	 */
	public abstract GroupId id();

	/**
	 * The group name.
	 * 
	 * @return a non-null group name
	 */
	@Nullable
	public abstract String name();

	/**
	 * The number of members in the group.
	 * 
	 * @return a non-negative group size
	 */
	public abstract int size();

	/**
	 * This group's child groups.
	 * 
	 * @return a non-null list of child groups
	 */
	@JsonProperty("child_groups")
	public abstract Set<GroupId> childGroups();

	/**
	 * Describes how this group is auto updated through user interaction. Is
	 * <code>null</code> if the group is not auto updated.
	 * 
	 * @return an auto update description
	 */
	@JsonProperty("auto_update")
	@Nullable
	public abstract AutoUpdate autoUpdate();

	/**
	 * The time at which this group was created
	 * 
	 * @return a date time
	 */
	@JsonProperty("created_at")
	public abstract OffsetDateTime createdAt();

	/**
	 * The time at which this group was last modified.
	 * 
	 * @return a date time
	 */
	@JsonProperty("modified_at")
	public abstract OffsetDateTime modifiedAt();

}
