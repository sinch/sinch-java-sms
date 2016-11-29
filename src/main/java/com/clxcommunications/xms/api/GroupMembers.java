package com.clxcommunications.xms.api;

import java.util.Arrays;
import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = GroupMembersImpl.class)
public abstract class GroupMembers {

	/**
	 * The MSISDNs that belong to a group.
	 * 
	 * @return a non-null list of group members
	 */
	public abstract Set<String> members();

	public static GroupMembers of(Iterable<String> members) {
		return GroupMembersImpl.of(members);
	}

	public static GroupMembers of(String... members) {
		return GroupMembersImpl.of(Arrays.asList(members));
	}

}
