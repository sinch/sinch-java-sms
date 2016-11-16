package com.clxcommunications.xms.api;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.clxcommunications.xms.ValueStylePublic;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(as = ImmutableMtBatchTextSmsResult.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsResult extends MtBatchSmsResult {

	public abstract String body();

	@Nullable
	public abstract String campaignId();

	@JsonInclude(Include.NON_EMPTY)
	public abstract Map<String, ParameterValues> parameters();

}
