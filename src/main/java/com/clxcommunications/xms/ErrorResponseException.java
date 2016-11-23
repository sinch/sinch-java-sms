package com.clxcommunications.xms;

import com.clxcommunications.xms.api.ApiError;

/**
 * Exception representing an error response from XMS. This exception is thrown
 * when some contract of the XMS API has been broken, the error code and text
 * indicates the specifics.
 * <p>
 * For information about specific errors please refer to the API documentation.
 * (TODO reference documentation URL)
 */
public class ErrorResponseException extends ApiException {

	private static final long serialVersionUID = 1L;

	private final String code;
	private final String text;

	ErrorResponseException(ApiError error) {
		super(error.text());

		this.code = error.code();
		this.text = error.text();
	}

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

}
