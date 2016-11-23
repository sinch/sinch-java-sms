package com.clxcommunications.xms;

import org.apache.http.HttpResponse;

/**
 * Exception thrown when an API connection receives a response from the REST API
 * that is neither a success response nor an API level error.
 */
public class UnexpectedResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The troublesome response.
	 */
	private final HttpResponse response;

	/**
	 * Creates a new unexpected response exception.
	 * 
	 * @param response
	 *            the response that was unexpected
	 */
	public UnexpectedResponseException(final HttpResponse response) {
		super("received unexpected response having status "
		        + getStatusCodeOrThrow(response));

		this.response = response;
	}

	private static int getStatusCodeOrThrow(final HttpResponse response) {
		return Utils.requireNonNull(response, "response")
		        .getStatusLine()
		        .getStatusCode();
	}

	/**
	 * Returns the HTTP response that could not be handled.
	 * 
	 * @return a HTTP response
	 */
	public HttpResponse getResponse() {
		return response;
	}

}
