package com.clxcommunications.xms;

import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.Nonnull;

/**
 * This class holds a number of static convenience methods for use within the
 * package. They are not intended for public exposure.
 */
final class Utils {

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
	static String join(String delim, String... strings) {
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
	static String join(String delim, Iterable<String> strings) {
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

}
