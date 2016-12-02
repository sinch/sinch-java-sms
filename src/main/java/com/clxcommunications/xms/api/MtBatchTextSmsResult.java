package com.clxcommunications.xms.api;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchTextSmsResult.Builder.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsResult extends MtBatchSmsResult {

	public static class Builder extends MtBatchTextSmsResultImpl.Builder {

	}

	public abstract String body();

	@JsonInclude(Include.NON_EMPTY)
	public abstract Map<String, ParameterValues> parameters();

}
