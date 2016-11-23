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
	 * wrapped in a {@link ConcurrentException}, which is subsequently returned. Since
	 * {@link ConcurrentException} is returned we facilitate the following use:
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
	 * @throws ExecutionException
	 */
	static ConcurrentException maybeUnwrapExecutionException(ExecutionException e)
	        throws ErrorResponseException, UnexpectedResponseException {
		if (e.getCause() instanceof RuntimeException) {
			throw (RuntimeException) e.getCause();
		} else if (e.getCause() instanceof Error) {
			throw (Error) e.getCause();
		} else if (e.getCause() instanceof ErrorResponseException) {
			throw (ErrorResponseException) e.getCause();
		} else if (e.getCause() instanceof UnexpectedResponseException) {
			throw (UnexpectedResponseException) e.getCause();
		} else {
			return new ConcurrentException(e.getCause());
		}
	}

}
