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

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.UpdateValue;
import com.clxcommunications.xms.Utils;

public class MtBatchTextSmsUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsUpdate input = ClxApi.batchTextSmsUpdate().build();

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

		params.put("newparam", ClxApi.parameterValues()
		        .putSubstitution("key1", "value1")
		        .build());

		MtBatchSmsUpdate input = ClxApi.batchTextSmsUpdate()
		        .from("1234")
		        .addRecipientInsertion("987654321")
		        .body("Hello, world!")
		        .parameters(UpdateValue.set(params))
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to_add': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': { 'newparam': { 'key1': 'value1' } }",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeWithUnsetParameters() throws Exception {
		MtBatchSmsUpdate input = ClxApi.batchTextSmsUpdate()
		        .from("1234")
		        .addRecipientInsertion("987654321")
		        .body("Hello, world!")
		        .unsetParameters()
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_text',",
		        "  'from': '1234',",
		        "  'to_add': [ '987654321' ],",
		        "  'body': 'Hello, world!',",
		        "  'parameters': null",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

}
