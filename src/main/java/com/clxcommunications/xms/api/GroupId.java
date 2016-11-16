package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Simple representation of a group identifier.
 */
@Value.Immutable
@ValueStylePackageDirect
public abstract class GroupId {

	@JsonValue
	public abstract String id();

	@JsonCreator
	public static GroupId of(String id) {
		return GroupIdImpl.of(id);
	}

}
