/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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

		MtBatchSmsResult actual =
		        json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParameters() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.parameterValues()
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
		                ClxApi.parameterValues()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual =
		        json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParametersAndDefault() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder()
		        .putParameter("param1",
		                ClxApi.parameterValues()
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
		                ClxApi.parameterValues()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .defaultValue("baz")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual =
		        json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	private static MtBatchTextSmsResult.Builder minimalBatchBuilder() {
		return new MtBatchTextSmsResult.Builder()
		        .sender("1234")
		        .addRecipient("987654321")
		        .body("Hello, world!")
		        .canceled(false)
		        .id(TestUtils.freshBatchId());
	}

}
