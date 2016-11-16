package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.runner.RunWith;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class ApiErrorTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Property
	public void canSerializeJson(String code, String text) throws Exception {
		ApiError input = ApiError.of(code, text);

		String expected = Utils.join("\n",
		        "{",
		        "  \"code\" : \"" + StringEscapeUtils.escapeJson(code) + "\",",
		        "  \"text\" : \"" + StringEscapeUtils.escapeJson(text) + "\"",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Property
	public void canDeserializeJson(String code, String text) throws Exception {
		ApiError expected = ApiError.of(code, text);

		String input = json.writeValueAsString(expected);

		ApiError actual = json.readValue(input, ApiError.class);

		assertThat(actual, is(expected));
	}

}
