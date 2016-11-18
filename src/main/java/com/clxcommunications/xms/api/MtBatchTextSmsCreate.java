package com.clxcommunications.xms.api;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(as = MtBatchTextSmsCreateImpl.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsCreate extends MtBatchSmsCreate {

	public abstract String body();

	@JsonInclude(Include.NON_EMPTY)
	public abstract Map<String, ParameterValues> parameters();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
