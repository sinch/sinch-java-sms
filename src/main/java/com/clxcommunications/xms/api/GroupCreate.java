package com.clxcommunications.xms.api;

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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = GroupCreate.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public interface GroupCreate {

	public static final class Builder extends GroupCreateImpl.Builder {

	}

	/**
	 * The group name.
	 * 
	 * @return the group name
	 */
	@Nullable
	String name();

	/**
	 * The MSISDNs that belong to this group.
	 * 
	 * @return a non-null list of group members
	 */
	@Nonnull
	Set<String> members();

	/**
	 * A collection of child groups that belong to this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@Nonnull
	@JsonProperty("child_groups")
	Set<GroupId> childGroups();

	/**
	 * Describes how this group should be auto updated. May be <code>null</code>
	 * if no auto update is to be supported.
	 * 
	 * @return an auto update description
	 */
	@Nullable
	@JsonProperty("auto_update")
	AutoUpdate autoUpdate();

	/**
	 * The tags associated to this group.
	 * 
	 * @return a non-null set of tags
	 */
	@Nonnull
	Set<String> tags();

}
