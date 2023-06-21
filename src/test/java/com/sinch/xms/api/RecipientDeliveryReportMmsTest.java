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

import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.Utils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Test;

public class RecipientDeliveryReportMmsTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerialize() throws Exception {
    OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);
    OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    RecipientDeliveryReport input =
        new RecipientDeliveryReportMms.Builder()
            .batchId(BatchId.of("batchid"))
            .recipient("12345")
            .code(10)
            .status(DeliveryStatus.DELIVERED)
            .statusMessage("status message")
            .at(time1)
            .operatorStatusAt(time2)
            .operator("818181")
            .encoding("GSM")
            .clientReference("client_ref")
            .numberOfMessageParts(1)
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'batch_id': 'batchid',",
                "  'recipient': '12345',",
                "  'type': 'recipient_delivery_report_mms',",
                "  'code': 10,",
                "  'status': 'Delivered',",
                "  'status_message': 'status message',",
                "  'operator': '818181',",
                "  'at': '2016-10-02T09:34:28.542Z',",
                "  'operator_status_at': '2016-11-02T09:34:28.542Z',",
                "  'client_reference': 'client_ref',",
                "  'encoding': 'GSM',",
                "  'number_of_message_parts': 1",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeMandatoryFields() throws Exception {
    OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    RecipientDeliveryReport input =
        new RecipientDeliveryReportMms.Builder()
            .batchId(BatchId.of("batchid"))
            .recipient("12345")
            .code(10)
            .status(DeliveryStatus.DELIVERED)
            .at(time1)
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'batch_id': 'batchid',",
                "  'recipient': '12345',",
                "  'type': 'recipient_delivery_report_mms',",
                "  'code': 10,",
                "  'status': 'Delivered',",
                "  'at': '2016-10-02T09:34:28.542Z'",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserialize() throws Exception {
    OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);
    OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    RecipientDeliveryReport expected =
        new RecipientDeliveryReportMms.Builder()
            .batchId(BatchId.of("batchid"))
            .recipient("1235")
            .code(10)
            .status(DeliveryStatus.DELIVERED)
            .statusMessage("status message")
            .at(time1)
            .operatorStatusAt(time2)
            .encoding("GSM")
            .numberOfMessageParts(1)
            .clientReference("client_ref")
            .build();

    String input = json.writeValueAsString(expected);

    RecipientDeliveryReportMms actual = json.readValue(input, RecipientDeliveryReportMms.class);

    assertThat(actual, is(expected));
  }

  @Test
  public void canDeserializeMandatoryFields() throws Exception {
    OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    RecipientDeliveryReport expected =
        new RecipientDeliveryReportMms.Builder()
            .batchId(BatchId.of("batchid"))
            .recipient("1235")
            .code(10)
            .status(DeliveryStatus.DELIVERED)
            .at(time1)
            .build();

    String input = json.writeValueAsString(expected);

    RecipientDeliveryReportMms actual = json.readValue(input, RecipientDeliveryReportMms.class);

    assertThat(actual, is(expected));
  }
}
