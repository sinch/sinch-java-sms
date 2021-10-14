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

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class BatchDeliveryReportTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeSummary() throws Exception {
    BatchDeliveryReport input =
        new BatchDeliveryReport.Builder()
            .batchId(BatchId.of("batchid"))
            .totalMessageCount(50)
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(10)
                    .status(DeliveryStatus.DELIVERED)
                    .count(20)
                    .build())
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(20)
                    .status(DeliveryStatus.FAILED)
                    .count(30)
                    .build())
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'batch_id': 'batchid',",
                "  'total_message_count': 50,",
                "  'type': 'delivery_report_sms',",
                "  'statuses': [",
                "    {",
                "      'code': 10,",
                "      'status': 'Delivered',",
                "      'count': 20",
                "    },",
                "    {",
                "      'code': 20,",
                "      'status': 'Failed',",
                "      'count': 30",
                "    }",
                "  ]",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeFull() throws Exception {
    BatchDeliveryReport input =
        new BatchDeliveryReport.Builder()
            .batchId(BatchId.of("batchid"))
            .totalMessageCount(50)
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(10)
                    .status(DeliveryStatus.DELIVERED)
                    .count(20)
                    .addRecipient("to1", "to2")
                    .build())
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(20)
                    .status(DeliveryStatus.FAILED)
                    .count(30)
                    .addRecipient("to3", "to4", "to5")
                    .build())
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'batch_id': 'batchid',",
                "  'total_message_count': 50,",
                "  'type': 'delivery_report_sms',",
                "  'statuses': [",
                "    {",
                "      'code': 10,",
                "      'status': 'Delivered',",
                "      'count': 20,",
                "      'recipients': [ 'to1', 'to2' ]",
                "    },",
                "    {",
                "      'code': 20,",
                "      'status': 'Failed',",
                "      'count': 30,",
                "      'recipients': [ 'to3', 'to4', 'to5' ]",
                "    }",
                "  ]",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Property
  public void canDeserialize(BatchId batchId) throws Exception {
    BatchDeliveryReport expected =
        new BatchDeliveryReport.Builder()
            .batchId(batchId)
            .totalMessageCount(50)
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(10)
                    .status(DeliveryStatus.DELIVERED)
                    .count(20)
                    .build())
            .addStatus(
                new BatchDeliveryReport.Status.Builder()
                    .code(20)
                    .status(DeliveryStatus.FAILED)
                    .count(30)
                    .build())
            .build();

    String input = json.writeValueAsString(expected);

    BatchDeliveryReport actual = json.readValue(input, BatchDeliveryReport.class);

    assertThat(actual, is(expected));
  }
}
