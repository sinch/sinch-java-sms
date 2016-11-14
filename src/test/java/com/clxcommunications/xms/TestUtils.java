package com.clxcommunications.xms;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A few utils that are handy to have around in the test suite.
 */
final class TestUtils {

	public static final Charset US_ASCII = Charset.forName("US-ASCII");

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Field used by {@link #freshUsername()} and {@link #freshBatchId()} to
	 * generate unique values.
	 */
	private static final AtomicInteger uniqueCounter = new AtomicInteger();

	/**
	 * Creates and returns a fresh unique SMS identifier.
	 * 
	 * @return a unique, non-null, message identifier
	 */
	static String freshSmsId() {
		return "sms" + uniqueCounter.incrementAndGet();
	}

	/**
	 * Creates and returns a fresh unique username.
	 * 
	 * @return a unique, non-null, username
	 */
	static String freshUsername() {
		return "user" + uniqueCounter.incrementAndGet();
	}

	/**
	 * Creates and returns a fresh batch ID. Guaranteed unique for this run.
	 * 
	 * @return a unique, non-null, batch ID.
	 */
	static BatchId freshBatchId() {
		return BatchId.of("batch" + uniqueCounter.incrementAndGet());
	}

	/**
	 * Hamcrest matcher that verifies that two strings describe the same JSON
	 * content.
	 * 
	 * @param expected
	 *            a JSON encoded string
	 * @return a non-null Hamcrest matcher
	 */
	static Matcher<String> jsonEqualTo(final String expected) {
		return new BaseMatcher<String>() {

			private final ObjectMapper om = new ObjectMapper();

			{
				om.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
				om.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			}

			private JsonNode readJsonNode(String expected) {
				try {
					return om.readValue(expected, JsonNode.class);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public boolean matches(Object item) {
				if (item == null) {
					return false;
				}

				if (!(item instanceof String)) {
					return false;
				}

				JsonNode expectedNode = readJsonNode(expected);
				JsonNode actualNode = readJsonNode((String) item);

				return expectedNode.equals(actualNode);
			}

			@Override
			public void describeTo(Description description) {
				description.appendValue(readJsonNode(expected));
			}

			@Override
			public void describeMismatch(Object item, Description description) {
				if (item instanceof String) {
					item = readJsonNode((String) item);
				}

				super.describeMismatch(item, description);
			}

		};
	}

}
