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
package com.clxcommunications.xms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

/**
 * This class holds a number of static convenience methods for use within the
 * SDK. That is, these methods are not considered part of the public API of this
 * library and their behavior may change without notice!
 */
public final class Utils {

	/**
	 * Convenience method that joins the given strings using the given
	 * delimiter.
	 * 
	 * @param delim
	 *            the delimiter
	 * @param strings
	 *            the non-null strings to join
	 * @return the joined string
	 */
	@Nonnull
	public static String join(String delim, String... strings) {
		requireNonNull(strings, "strings");

		return join(delim, Arrays.asList(strings));
	}

	/**
	 * Convenience method that joins the given strings using the given
	 * delimiter.
	 * 
	 * @param delim
	 *            the delimiter
	 * @param strings
	 *            the non-null strings to join
	 * @return the joined string
	 */
	@Nonnull
	public static String join(String delim, Iterable<String> strings) {
		requireNonNull(delim, "delim");
		requireNonNull(strings, "strings");

		StringBuilder sb = new StringBuilder(50);

		Iterator<String> it = strings.iterator();
		if (it.hasNext()) {
			sb.append(it.next());

			while (it.hasNext()) {
				sb.append(delim).append(it.next());
			}
		}

		return sb.toString();
	}

	static <T> T requireNonNull(T o, String name) {
		if (o == null) {
			throw new NullPointerException(name);
		}

		return o;
	}

	/**
	 * Unwrap exceptions for synchronous send methods. This helper will examine
	 * an {@link ExecutionException} and re-throw its cause. Beside the checked
	 * exception shown in the method signature this method will also unwrap any
	 * {@link RuntimeException runtime exception} or {@link Error error} and
	 * directly re-throw it.
	 * <p>
	 * For checked exceptions we directly throw {@link ErrorResponseException}
	 * and {@link UnexpectedResponseException}. Any other checked exception is
	 * wrapped in a {@link ConcurrentException}, which is subsequently returned.
	 * Since {@link ConcurrentException} is returned we facilitate the following
	 * use:
	 * 
	 * <pre>
	 * try {
	 *     // …
	 * } catch (ExecutionException e) {
	 *     throw maybeUnwrapExecutionException(e);
	 * }
	 * </pre>
	 * 
	 * @param e
	 *            the exception to examine
	 * @return returns <code>e</code>
	 * @throws ErrorResponseException
	 * @throws UnexpectedResponseException
	 * @throws UnauthorizedException
	 * @throws ExecutionException
	 */
	static ConcurrentException unwrapExecutionException(ExecutionException e)
	        throws ErrorResponseException, UnexpectedResponseException,
	        UnauthorizedException {
		if (e.getCause() instanceof RuntimeException) {
			throw (RuntimeException) e.getCause();
		} else if (e.getCause() instanceof Error) {
			throw (Error) e.getCause();
		} else if (e.getCause() instanceof ErrorResponseException) {
			throw (ErrorResponseException) e.getCause();
		} else if (e.getCause() instanceof UnexpectedResponseException) {
			throw (UnexpectedResponseException) e.getCause();
		} else if (e.getCause() instanceof UnauthorizedException) {
			throw (UnauthorizedException) e.getCause();
		} else {
			return new ConcurrentException(e.getCause());
		}
	}

}
