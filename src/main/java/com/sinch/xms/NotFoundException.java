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

import javax.annotation.Nonnull;

/**
 * Exception thrown when an API connection receives a 404 response from the REST
 * API. This typically indicates that the desired resource was not found due to,
 * e.g., an incorrect batch or group ID.
 */
public class NotFoundException extends ApiException {

	private static final long serialVersionUID = 1L;

	private final String path;

	/**
	 * Creates a new not found exception.
	 * 
	 * @param path
	 *            the invalid path
	 */
	public NotFoundException(@Nonnull String path) {
		super("Resource not found at " + Utils.requireNonNull(path, "path"));

		this.path = path;
	}

	/**
	 * Returns the REST API path that did not exist.
	 * 
	 * @return the invalid path
	 */
	@Nonnull
	public String getPath() {
		return path;
	}

}
