package com.clxcommunications.xms.api;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = ImmutableGroupMembers.class)
public abstract class GroupMembers {

	/**
	 * The MSISDNs that belong to a group.
	 * 
	 * @return a non-null list of group members
	 */
	public abstract Set<String> members();

	public static GroupMembers of(Iterable<String> members) {
		return ImmutableGroupMembers.of(members);
	}

}
