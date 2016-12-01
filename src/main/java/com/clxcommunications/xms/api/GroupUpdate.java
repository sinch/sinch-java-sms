package com.clxcommunications.xms.api;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.clxcommunications.xms.UpdateValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonSerialize(as = GroupUpdateImpl.class)
@JsonInclude(Include.NON_EMPTY)
public interface GroupUpdate {

	public static class Builder extends GroupUpdateImpl.Builder {

		public Builder unsetName() {
			return this.name(UpdateValue.<String> unset());
		}

		public Builder name(String name) {
			if (name == null) {
				return this.unsetName();
			} else {
				return this.name(UpdateValue.set(name));
			}
		}

		public Builder unsetAutoUpdate() {
			return this.autoUpdate(UpdateValue.<AutoUpdate> unset());
		}

		public Builder autoUpdate(AutoUpdate autoUpdate) {
			if (autoUpdate == null) {
				return this.unsetAutoUpdate();
			} else {
				return this.autoUpdate(UpdateValue.set(autoUpdate));
			}
		}

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
	public abstract Set<String> memberAdd();

	/**
	 * The MSISDNs that should be removed from this group.
	 * 
	 * @return a non-null list of group members
	 */
	@JsonProperty("remove")
	public abstract Set<String> memberRemove();

	/**
	 * The child groups that should be added to this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@JsonProperty("child_groups_add")
	public abstract Set<GroupId> childGroupsAdd();

	/**
	 * The child groups that should be removed from this group.
	 * 
	 * @return a non-null list of group IDs
	 */
	@JsonProperty("child_groups_remove")
	public abstract Set<GroupId> childGroupsRemove();

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
