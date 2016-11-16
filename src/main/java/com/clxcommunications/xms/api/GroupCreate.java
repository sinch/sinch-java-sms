package com.clxcommunications.xms.api;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(builder = GroupCreateImpl.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public interface GroupCreate {

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
	Set<String> childGroups();

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
