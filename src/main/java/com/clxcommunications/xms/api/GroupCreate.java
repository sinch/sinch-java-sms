/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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
package com.clxcommunications.xms.api;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A class describing parameters available during group creation. Groups can be
 * used within XMS to organize message recipients and easily send batch messages
 * to all members but referring to the group identifier as message recipient.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = GroupCreate.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public abstract class GroupCreate {

	/**
	 * A builder of group creation descriptions.
	 */
	public static final class Builder extends GroupCreateImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link GroupCreate} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final GroupCreate.Builder builder() {
		return new Builder();
	}

	/**
	 * The group name.
	 * 
	 * @return the group name
	 */
	@Nullable
	public abstract String name();

	/**
	 * The MSISDNs that belong to this group.
	 * 
	 * @return a non-null list of group members
	 */
	@Nonnull
	public abstract Set<String> members();

	/**
	 * A collection of child groups that belong to this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@Nonnull
	@JsonProperty("child_groups")
	public abstract Set<GroupId> childGroups();

	/**
	 * Describes how this group is auto updated through user interaction. Is
	 * <code>null</code> if the group is not auto updated.
	 * 
	 * @return an auto update description
	 */
	@Nullable
	@JsonProperty("auto_update")
	public abstract AutoUpdate autoUpdate();

	/**
	 * The tags associated to this group.
	 * 
	 * @return a non-null set of tags
	 */
	@Nonnull
	public abstract Set<String> tags();

}
