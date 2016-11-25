package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedBatchResult.Builder.class)
public abstract class PagedBatchResult extends Page<MtBatchSmsResult> {

	public static class Builder extends PagedBatchResultImpl.Builder {

	}

	@JsonProperty("batches")
	@Override
	public abstract List<MtBatchSmsResult> content();

}
