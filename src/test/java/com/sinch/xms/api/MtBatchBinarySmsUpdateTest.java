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

import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.UpdateValue;
import com.sinch.xms.Utils;

public class MtBatchBinarySmsUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsUpdate input = SinchSMSApi.batchBinarySmsUpdate().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeWithUpdatedFields() throws Exception {
		MtBatchSmsUpdate input = SinchSMSApi.batchBinarySmsUpdate()
		        .sender("1234")
		        .addRecipientInsertion("987654321")
		        .body("Hello, world!".getBytes(TestUtils.US_ASCII))
		        .udh(new byte[] { 1, 2, 3, 4 })
		        .deliveryReport(UpdateValue.<ReportType> unset())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary',",
		        "  'from': '1234',",
		        "  'to_add': [ '987654321' ],",
		        "  'body': 'SGVsbG8sIHdvcmxkIQ==',",
		        "  'udh': '01020304',",
		        "  'delivery_report': null",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

}
