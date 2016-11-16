package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;

public class MtBatchTextSmsTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSms input = minimalBatchBuilder().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		MtBatchSms expected = minimalBatchBuilder().build();

		String input = json.writeValueAsString(expected);

		MtBatchSms actual = json.readValue(input, MtBatchSms.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParameters() throws Exception {
		MtBatchSms input = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .build())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': {",
		        "    'param1': {",
		        "      '123': 'foo',",
		        "      '234': 'bar'",
		        "    }",
		        "  }",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeWithParameters() throws Exception {
		MtBatchSms expected = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSms actual = json.readValue(input, MtBatchSms.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParametersAndDefault() throws Exception {
		MtBatchSms input = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .defaultValue("baz")
		                        .build())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': {",
		        "    'param1': {",
		        "      '123': 'foo',",
		        "      '234': 'bar',",
		        "      'default': 'baz'",
		        "    }",
		        "  }",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeWithParametersAndDefault() throws Exception {
		MtBatchSms expected = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .defaultValue("baz")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSms actual = json.readValue(input, MtBatchSms.class);

		assertThat(actual, is(expected));
	}

	private static ImmutableMtBatchTextSms.Builder minimalBatchBuilder() {
		return ClxApi.buildBatchTextSms()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!");
	}

}
