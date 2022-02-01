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
import org.junit.Test;

public class MtBatchBinarySmsResultTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeMinimal() throws Exception {
    MtBatchSmsResult input = minimalBatchBuilder().build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'type': 'mt_binary',",
                "  'from': '1234',",
                "  'to': [ '987654321' ],",
                "  'canceled': false,",
                "  'feedback_enabled': false,",
                "  'flash_message': false,",
                "  'delivery_report': 'none',",
                "  'id': '" + input.id() + "',",
                "  'body': 'SGVsbG8sIHdvcmxkIQ==',",
                "  'udh': '01020304'",
                "}")
            .replace('\'', '"');

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

  private static MtBatchBinarySmsResult.Builder minimalBatchBuilder() {
    return new MtBatchBinarySmsResult.Builder()
        .sender("1234")
        .addRecipient("987654321")
        .flashMessage(false)
        .feedbackEnabled(false)
        .deliveryReport(ReportType.NONE)
        .body("Hello, world!".getBytes(TestUtils.US_ASCII))
        .udh(new byte[] {1, 2, 3, 4})
        .canceled(false)
        .id(TestUtils.freshBatchId());
  }
}
