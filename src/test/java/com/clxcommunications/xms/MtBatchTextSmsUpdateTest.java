package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class MtBatchTextSmsUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsUpdate input = ClxApi.buildBatchTextSmsUpdate().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeWithUpdatedParameters() throws Exception {
		Map<String, ParameterValues> params =
		        new TreeMap<String, ParameterValues>();

		params.put("newparam", ClxApi.buildSubstitution()
		        .putSubstitution("key1", "value1")
		        .build());

		MtBatchSmsUpdate input = ClxApi.buildBatchTextSmsUpdate()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!")
		        .parameters(UpdateValue.set(params))
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': { 'newparam': { 'key1': 'value1' } }",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeWithUnsetParameters() throws Exception {
		MtBatchSmsUpdate input = ClxApi.buildBatchTextSmsUpdate()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!")
		        .unsetParameters()
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': null",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

}
