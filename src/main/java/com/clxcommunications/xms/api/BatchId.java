package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Value.Immutable
@ValueStylePackageDirect
public abstract class BatchId {

	@JsonValue
	public abstract String id();

	@JsonCreator
	public static BatchId of(String id) {
		return BatchIdImpl.of(id);
	}

}
