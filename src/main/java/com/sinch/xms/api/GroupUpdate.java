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

import com.sinch.xms.UpdateValue;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A description of updates that should be applied to a group.
 */
@Value.Immutable
@ValueStylePackage
@JsonSerialize(as = GroupUpdateImpl.class)
@JsonInclude(Include.NON_EMPTY)
public abstract class GroupUpdate {

	/**
	 * A builder of group updates.
	 */
	public static class Builder extends GroupUpdateImpl.Builder {

		Builder() {
		}

		/**
		 * Unsets the name of the group.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetName() {
			return this.name(UpdateValue.<String> unset());
		}

		/**
		 * Updates the group name to the given name.
		 * 
		 * @param name
		 *            the new group name
		 * @return this builder for use in a chained invocation
		 */
		public Builder name(String name) {
			if (name == null) {
				return this.unsetName();
			} else {
				return this.name(UpdateValue.set(name));
			}
		}

		/**
		 * Unsets the group auto update setting.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetAutoUpdate() {
			return this.autoUpdate(UpdateValue.<AutoUpdate> unset());
		}

		/**
		 * Updates the group auto update to the one given.
		 * 
		 * @param autoUpdate
		 *            the new auto update setting
		 * @return this builder for use in a chained invocation
		 */
		public Builder autoUpdate(AutoUpdate autoUpdate) {
			if (autoUpdate == null) {
				return this.unsetAutoUpdate();
			} else {
				return this.autoUpdate(UpdateValue.set(autoUpdate));
			}
		}

	}

	/**
	 * Creates a builder of {@link GroupUpdate} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final GroupUpdate.Builder builder() {
		return new Builder();
	}

	/**
	 * The group name.
	 * 
	 * @return the group name
	 */
	@Nullable
	public abstract UpdateValue<String> name();

	/**
	 * The MSISDNs that should be added to this group.
	 * 
	 * @return a non-null list of group members
	 */
	@JsonProperty("add")
	public abstract Set<String> memberInsertions();

	/**
	 * The MSISDNs that should be removed from this group.
	 * 
	 * @return a non-null list of group members
	 */
	@JsonProperty("remove")
	public abstract Set<String> memberRemovals();

	/**
	 * The child groups that should be added to this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@JsonProperty("child_groups_add")
	public abstract Set<GroupId> childGroupInsertions();

	/**
	 * The child groups that should be removed from this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@JsonProperty("child_groups_remove")
	public abstract Set<GroupId> childGroupRemovals();

	/**
	 * Identifier of a group whose members should be added to this group.
	 * 
	 * @return a group ID
	 */
	@Nullable
	@JsonProperty("add_from_group")
	public abstract GroupId addFromGroup();

	/**
	 * Identifier of a group whose members should be removed to this group.
	 * 
	 * @return a group ID
	 */
	@Nullable
	@JsonProperty("remove_from_group")
	public abstract GroupId removeFromGroup();

	/**
	 * Describes how this group should be auto updated. May be <code>null</code>
	 * if no auto update is to be supported.
	 * 
	 * @return an auto update description
	 */
	@Nullable
	@JsonProperty("auto_update")
	public abstract UpdateValue<AutoUpdate> autoUpdate();

}
