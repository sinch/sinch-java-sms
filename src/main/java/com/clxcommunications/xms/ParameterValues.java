package com.clxcommunications.xms;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(using = JacksonUtils.ParameterValuesDeserializer.class)
@JsonSerialize(using = JacksonUtils.ParameterValuesSerializer.class)
public abstract class ParameterValues {

	public abstract Map<String, String> substitutions();

	@Nullable
	public abstract String defaultValue();

}
