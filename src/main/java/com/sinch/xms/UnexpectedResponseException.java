/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sinch.xms;

import org.apache.http.HttpResponse;

/**
 * Exception thrown when an API connection receives a response from the REST API
 * that is neither a success response nor an API level error.
 */
public class UnexpectedResponseException extends ApiException {

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
