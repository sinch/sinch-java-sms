package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.Utils;

public class MtBatchTextSmsResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'canceled': false,",
		        "  'id': '" + input.id() + "'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		MtBatchSmsResult expected = minimalBatchBuilder().build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual = json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParameters() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder()
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
		        "  'canceled': false,",
		        "  'id': '" + input.id() + "',",
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
		MtBatchSmsResult expected = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual = json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParametersAndDefault() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder()
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
		        "  'canceled': false,",
		        "  'id': '" + input.id() + "',",
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
		MtBatchSmsResult expected = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.buildSubstitution()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .defaultValue("baz")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual = json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	private static MtBatchTextSmsResultImpl.Builder minimalBatchBuilder() {
		return new MtBatchTextSmsResult.Builder()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!")
		        .canceled(false)
		        .id(TestUtils.freshBatchId());
	}

}
