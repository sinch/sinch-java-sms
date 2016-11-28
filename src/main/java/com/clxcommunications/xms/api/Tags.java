package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = TagsImpl.Builder.class)
public abstract class Tags {

	public abstract List<String> tags();

	public static final Tags of(Iterable<String> tags) {
		return TagsImpl.builder().tags(tags).build();
	}

	public static final Tags of(String... tags) {
		return TagsImpl.builder().addTag(tags).build();
	}

}
