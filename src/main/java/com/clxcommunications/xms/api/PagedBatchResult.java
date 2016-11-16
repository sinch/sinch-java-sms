package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(builder = ImmutablePagedBatchResult.Builder.class)
public abstract class PagedBatchResult extends Page<MtBatchSmsResult> {

	public abstract List<MtBatchSmsResult> batches();

	@JsonIgnore
	@Override
	protected final List<MtBatchSmsResult> content() {
		return batches();
	}

}
