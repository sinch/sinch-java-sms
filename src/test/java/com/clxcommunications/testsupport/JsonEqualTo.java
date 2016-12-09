package com.clxcommunications.testsupport;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Hamcrest matcher for textual JSON blobs.
 */
final class JsonEqualTo extends BaseMatcher<String> {

	private final String expected;

	private final ObjectMapper om = new ObjectMapper();

	JsonEqualTo(String expected) {
		this.expected = expected;

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

}
