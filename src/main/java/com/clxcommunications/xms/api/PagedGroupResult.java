package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedGroupResult.Builder.class)
public abstract class PagedGroupResult extends Page<GroupResponse> {

	public static class Builder extends PagedGroupResultImpl.Builder {

	}

	@JsonProperty("groups")
	@Override
	public abstract List<GroupResponse> content();

}
