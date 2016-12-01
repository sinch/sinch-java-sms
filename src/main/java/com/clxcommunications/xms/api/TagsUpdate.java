package com.clxcommunications.xms.api;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = TagsUpdate.Builder.class)
public interface TagsUpdate {

	public static final class Builder extends TagsUpdateImpl.Builder {

	}

	/**
	 * A set of tags to add to a given object.
	 * 
	 * @return a non-null list of tags
	 */
	@JsonProperty("add")
	Set<String> tagInsertions();

	/**
	 * A set of tags to remove from a given object.
	 * 
	 * @return a non-null list of tags
	 */
	@JsonProperty("remove")
	Set<String> tagRemovals();

}
