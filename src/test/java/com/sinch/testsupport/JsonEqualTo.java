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
package com.sinch.testsupport;

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
