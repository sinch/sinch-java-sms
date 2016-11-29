package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedInboundsResult.Builder.class)
public abstract class PagedInboundsResult extends Page<MoSms> {

	public static class Builder extends PagedInboundsResultImpl.Builder {

	}

	@JsonProperty("inbounds")
	@Override
	public abstract List<MoSms> content();

}
