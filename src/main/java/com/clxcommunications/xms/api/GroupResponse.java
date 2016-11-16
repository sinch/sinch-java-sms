package com.clxcommunications.xms.api;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(as = GroupResponseImpl.class)
@JsonInclude(Include.NON_EMPTY)
public interface GroupResponse {

	/**
	 * The unique group ID that identifies this group.
	 * 
	 * @return a non-null group ID
	 */
	GroupId id();

	/**
	 * The group name.
	 * 
	 * @return a non-null group name
	 */
	@Nullable
	String name();

	/**
	 * The number of members in the group.
	 * 
	 * @return a non-negative group size
	 */
	int size();

	/**
	 * This group's child groups.
	 * 
	 * @return a non-null list of child groups
	 */
	@JsonProperty("child_groups")
	Set<GroupId> childGroups();

	@JsonProperty("auto_update")
	@Nullable
	AutoUpdate autoUpdate();

	@JsonProperty("created_at")
	OffsetDateTime createdAt();

	@JsonProperty("modified_at")
	OffsetDateTime modifiedAt();

}
