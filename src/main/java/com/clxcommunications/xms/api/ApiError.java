package com.clxcommunications.xms.api;

import javax.annotation.Nonnull;

import org.immutables.value.Value;

import com.clxcommunications.xms.ApiException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * API object containing an error response.
 * 
 * @see ApiException
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = ApiErrorImpl.class)
public abstract class ApiError {

	/**
	 * A collection of known error codes. The REST API reserves the right to
	 * introduce new error codes as need arise so these may not be exhaustive.
	 */
	public static final class Code {

		/**
		 * The request body is not valid JSON.
		 */
		public static final String SYNTAX_INVALID_JSON =
		        "syntax_invalid_json";

		/**
		 * The format of a parameter is invalid. For example if an MSISDN is not
		 * properly formatted.
		 */
		public static final String SYNTAX_INVALID_PARAMETER_FORMAT =
		        "syntax_invalid_parameter_format";

		/**
		 * The request body doesn’t fulfill all of the constraints set by the
		 * API. For example missing required parameters or too many elements in
		 * a list.
		 */
		public static final String SYNTAX_CONSTRAINT_VIOLATION =
		        "syntax_constraint_violation";

		/**
		 * An unknown group was referenced in the request body.
		 */
		public static final String UNKNOWN_GROUP =
		        "unknown_group";

		/**
		 * When creating a group with a name that already exists.
		 */
		public static final String CONFLICT_GROUP_NAME =
		        "conflict_group_name";

		/**
		 * When deleting a message that has already been sent out.
		 */
		public static final String CONFLICT_SEND_AT =
		        "conflict_send_at";

	}

	/**
	 * The machine readable error code.
	 * 
	 * @return a non-null string
	 */
	public abstract String code();

	/**
	 * The human readable error message.
	 * 
	 * @return a non-null string
	 */
	public abstract String text();

	/**
	 * Creates a new API error object from the given code and text.
	 * 
	 * @param code
	 *            the error code
	 * @param text
	 *            the error message
	 * @return a non-null API error object
	 */
	@Nonnull
	public static ApiError of(String code, String text) {
		return ApiErrorImpl.of(code, text);
	}

}
