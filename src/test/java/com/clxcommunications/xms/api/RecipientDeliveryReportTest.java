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
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class RecipientDeliveryReportTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerialize() throws Exception {
		OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		RecipientDeliveryReport input =
		        new RecipientDeliveryReport.Builder()
		                .batchId(BatchId.of("batchid"))
		                .recipient("12345")
		                .code(10)
		                .status(DeliveryStatus.DELIVERED)
		                .statusMessage("status message")
		                .at(time1)
		                .operatorStatusAt(time2)
		                .operator("818181")
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'batch_id': 'batchid',",
		        "  'recipient': '12345',",
		        "  'type': 'recipient_delivery_report_sms',",
		        "  'code': 10,",
		        "  'status': 'Delivered',",
		        "  'status_message': 'status message',",
		        "  'operator': '818181',",
		        "  'at': '2016-10-02T09:34:28.542Z',",
		        "  'operator_status_at': '2016-11-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserialize() throws Exception {
		OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		RecipientDeliveryReport expected =
		        new RecipientDeliveryReport.Builder()
		                .batchId(BatchId.of("batchid"))
		                .recipient("1235")
		                .code(10)
		                .status(DeliveryStatus.DELIVERED)
		                .statusMessage("status message")
		                .at(time1)
		                .operatorStatusAt(time2)
		                .build();

		String input = json.writeValueAsString(expected);

		RecipientDeliveryReport actual =
		        json.readValue(input, RecipientDeliveryReport.class);

		assertThat(actual, is(expected));
	}

}
