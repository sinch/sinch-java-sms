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
package com.sinch.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.Utils;

public class MtBatchTextSmsCreateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsCreate input = minimalBatchBuilder().build();

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
		MtBatchSmsCreate expected = minimalBatchBuilder().build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsCreate actual =
		        json.readValue(input, MtBatchSmsCreate.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParameters() throws Exception {
		MtBatchSmsCreate input = minimalBatchBuilder()
		        .putParameter("param1",
		                SinchSMSApi.parameterValues()
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
		MtBatchSmsCreate expected = minimalBatchBuilder()
		        .putParameter("param1",
		                SinchSMSApi.parameterValues()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsCreate actual =
		        json.readValue(input, MtBatchSmsCreate.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithParametersAndDefault() throws Exception {
		MtBatchSmsCreate input = minimalBatchBuilder()
		        .putParameter("param1",
		                SinchSMSApi.parameterValues()
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
		MtBatchSmsCreate expected = minimalBatchBuilder()
		        .putParameter("param1",
		                SinchSMSApi.parameterValues()
		                        .putSubstitution("123", "foo")
		                        .putSubstitution("234", "bar")
		                        .defaultValue("baz")
		                        .build())
		        .build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsCreate actual =
		        json.readValue(input, MtBatchSmsCreate.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithDatesAndTags() throws Exception {
		MtBatchSmsCreate input = minimalBatchBuilder()
		        .sendAt(OffsetDateTime.of(2016, 12, 1, 10, 20, 30, 0,
		                ZoneOffset.UTC))
		        .expireAt(OffsetDateTime.of(2016, 12, 20, 10, 0, 0, 0,
		                ZoneOffset.UTC))
		        .addTag("tag1", "tag2")
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'send_at': '2016-12-01T10:20:30Z',",
		        "  'expire_at': '2016-12-20T10:00:00Z',",
		        "  'tags': ['tag1', 'tag2']",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test(expected = IllegalStateException.class)
	public void requiresToAddress() throws Exception {
		SinchSMSApi.batchTextSms()
		        .sender("1234")
		        .body("body")
		        .build();
	}

	@Test(expected = IllegalStateException.class)
	public void requiresNonEmptyToAddress() throws Exception {
		SinchSMSApi.batchTextSms()
		        .sender("1234")
		        .addRecipient("987654321")
		        .addRecipient("")
		        .body("body")
		        .build();
	}

	@Test(expected = IllegalStateException.class)
	public void requiresNonEmptyFromAddress() throws Exception {
		SinchSMSApi.batchTextSms()
		        .sender("")
		        .addRecipient("987654321")
		        .body("body")
		        .build();
	}

	private static MtBatchTextSmsCreate.Builder minimalBatchBuilder() {
		return SinchSMSApi.batchTextSms()
		        .sender("1234")
		        .addRecipient("987654321")
		        .body("Hello, world!");
	}

}
