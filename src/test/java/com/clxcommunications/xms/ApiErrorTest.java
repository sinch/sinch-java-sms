package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

public class ApiErrorTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJson() throws Exception {
		String code = RandomStringUtils.randomPrint(1, 20);
		String text = RandomStringUtils.randomPrint(1, 20);
		ApiError input = ImmutableApiError.of(code, text);

		String expected = Utils.join("\n",
		        "{",
		        "  \"code\" : \"" + StringEscapeUtils.escapeJson(code) + "\",",
		        "  \"text\" : \"" + StringEscapeUtils.escapeJson(text) + "\"",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJson() throws Exception {
		String code = RandomStringUtils.randomPrint(1, 20);
		String text = RandomStringUtils.randomPrint(1, 20);
		ApiError expected = ImmutableApiError.of(code, text);

		String input = json.writeValueAsString(expected);

		ApiError actual = json.readValue(input, ApiError.class);

		assertThat(actual, is(expected));
	}

}
