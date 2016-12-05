package com.clxcommunications.xms;

/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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

import com.clxcommunications.xms.api.ApiError;

/**
 * Exception representing an error response from XMS. This exception is thrown
 * when some contract of the XMS API has been broken, the error code and text
 * indicates the specifics.
 * <p>
 * For information about specific errors please refer to the <a href=
 * "https://manage.clxcommunications.com/developers/sms/xmsapi.html#http-errors">XMS
 * API documentation</a>.
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

	/**
	 * The machine readable error code.
	 * 
	 * @return the error code.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * The human readable error text.
	 * 
	 * @return the error text
	 */
	public String getText() {
		return text;
	}

}
