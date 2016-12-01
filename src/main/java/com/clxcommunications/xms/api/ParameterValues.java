package com.clxcommunications.xms.api;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(using = JacksonUtils.ParameterValuesDeserializer.class)
@JsonSerialize(using = JacksonUtils.ParameterValuesSerializer.class)
public interface ParameterValues {

	public static class Builder extends ParameterValuesImpl.Builder {

	}

	Map<String, String> substitutions();

	@Nullable
	String defaultValue();

}
