package com.clxcommunications.xms;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * API object containing an error response.
 * 
 * @see ApiException
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = ImmutableApiError.class)
public abstract class ApiError {

	public static final String SYNTAX_INVALID_JSON = "syntax_invalid_json";
	public static final String SYNTAX_INVALID_PARAMETER_FORMAT =
	        "syntax_invalid_parameter_format";
	public static final String SYNTAX_CONSTRAINT_VIOLATION =
	        "syntax_constraint_violation";
	public static final String UNKNOWN_GROUP = "unknown_group";
	public static final String UNKNOWN_CAMPAIGN = "unknown_campaign";
	public static final String CONFLICT_GROUP_NAME = "conflict_group_name";
	public static final String CONFLICT_SEND_AT = "conflict_send_at";

	public abstract String code();

	public abstract String text();

	public static ApiError of(String code, String text) {
		return ImmutableApiError.of(code, text);
	}

}
