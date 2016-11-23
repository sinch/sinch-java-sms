package com.clxcommunications.xms;

/**
 * Base class for all exceptions specific to the CLX SDK.
 */
public abstract class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

}
