package com.clxcommunications.xms.api;

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class PagedBatchResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJsonWithEmptyBatches() throws Exception {
		PagedBatchResult input =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(0)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 0,",
		        "  'count' : 0,",
		        "  'batches' : []",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithEmptyBatches() throws Exception {
		PagedBatchResult expected =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(0)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedBatchResult actual =
		        json.readValue(input, PagedBatchResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeJsonWithNonEmptyBatches() throws Exception {
		BatchId batchId1 = TestUtils.freshBatchId();
		BatchId batchId2 = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.now(Clock.systemUTC());
		String smsTimeString = json.writeValueAsString(smsTime);

		MtBatchSmsResult batchResult1 =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId1)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		MtBatchSmsResult batchResult2 =
		        new MtBatchBinarySmsResult.Builder()
		                .using(batchResult1)
		                .id(batchId2)
		                .body("foobar".getBytes(TestUtils.US_ASCII))
		                .udh(new byte[] { 1, 2, 3 })
		                .build();

		PagedBatchResult input =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(1)
		                .totalSize(0)
		                .addContent(batchResult1)
		                .addContent(batchResult2)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 1,",
		        "  'count' : 0,",
		        "  'batches' : [",
		        "    {",
		        "      'type' : 'mt_text',",
		        "      'to': [",
		        "        '123456789',",
		        "        '987654321'",
		        "      ],",
		        "      'body': 'Hello, world!',",
		        "      'canceled': false,",
		        "      'id': '" + batchId1 + "',",
		        "      'from': '12345',",
		        "      'created_at': " + smsTimeString + ",",
		        "      'modified_at': " + smsTimeString + "",
		        "    },",
		        "    {",
		        "      'type' : 'mt_binary',",
		        "      'to': [",
		        "        '123456789',",
		        "        '987654321'",
		        "      ],",
		        "      'body': 'Zm9vYmFy',",
		        "      'udh': '010203',",
		        "      'canceled': false,",
		        "      'id': '" + batchId2 + "',",
		        "      'from': '12345',",
		        "      'created_at': " + smsTimeString + ",",
		        "      'modified_at': " + smsTimeString + "",
		        "    }",
		        "  ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithNonEmptyBatches() throws Exception {
		BatchId batchId1 = TestUtils.freshBatchId();
		BatchId batchId2 = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.now(Clock.systemUTC());

		MtBatchSmsResult batchResult1 =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId1)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		MtBatchSmsResult batchResult2 =
		        new MtBatchBinarySmsResult.Builder()
		                .using(batchResult1)
		                .id(batchId2)
		                .body("Hello, again!".getBytes())
		                .udh("udh".getBytes())
		                .build();

		PagedBatchResult expected =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(1)
		                .totalSize(0)
		                .addContent(batchResult1)
		                .addContent(batchResult2)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedBatchResult actual =
		        json.readValue(input, PagedBatchResult.class);

		assertThat(actual, is(expected));
	}

}
