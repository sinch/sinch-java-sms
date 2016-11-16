package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = TagsUpdateImpl.class)
public interface TagsUpdate {

	/**
	 * A list of tags to add to a given object.
	 * 
	 * @return a non-null list of tags
	 */
	@JsonProperty("to_add")
	List<String> newTag();

	/**
	 * A list of tags to remove from a given object.
	 * 
	 * @return a non-null list of tags
	 */
	@JsonProperty("to_remove")
	List<String> removeTag();

}
