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
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.Utils;
import org.junit.Test;

public class MtBatchMmsCreateTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeMinimal() throws Exception {
    MtBatchMmsCreate input = minimalBatchBuilder().build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'type': 'mt_media',",
                "  'from': '1234',",
                "  'to': [ '987654321' ],",
                "  'body': {",
                "    'url':'http://my.test.url/image.jpg'",
                "  }",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeMinimal() throws Exception {
    MtBatchMmsCreate expected = minimalBatchBuilder().build();

    String input = json.writeValueAsString(expected);

    MtBatchMmsCreate actual = json.readValue(input, MtBatchMmsCreate.class);

    assertThat(actual, is(expected));
  }

  @Test
  public void canSerializeWithMessageContent() throws Exception {
    MtBatchMmsCreate input =
        SinchSMSApi.batchMms()
            .sender("1234")
            .addRecipient("987654321")
            .body(
                SinchSMSApi.mediaBody()
                    .url("http://my.test.url/image.jpg")
                    .message("the text")
                    .build())
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'type': 'mt_media',",
                "  'from': '1234',",
                "  'to': [ '987654321' ],",
                "  'body': {",
                "    'url':'http://my.test.url/image.jpg',",
                "    'message':'the text'",
                "  }",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeWithMessageContent() throws Exception {
    MtBatchMmsCreate expected =
        SinchSMSApi.batchMms()
            .sender("1234")
            .addRecipient("987654321")
            .body(
                SinchSMSApi.mediaBody()
                    .url("http://my.test.url/image.jpg")
                    .message("the text")
                    .build())
            .build();

    String input = json.writeValueAsString(expected);

    MtBatchMmsCreate actual = json.readValue(input, MtBatchMmsCreate.class);

    assertThat(actual, is(expected));
  }

  @Test(expected = IllegalStateException.class)
  public void requiresUrl() {
    SinchSMSApi.batchMms()
        .sender("1234")
        .addRecipient("987654321")
        .body(SinchSMSApi.mediaBody().message("Hello, world!").build())
        .build();
  }

  private static MtBatchMmsCreate.Builder minimalBatchBuilder() {
    return SinchSMSApi.batchMms()
        .sender("1234")
        .addRecipient("987654321")
        .body(SinchSMSApi.mediaBody().url("http://my.test.url/image.jpg").build());
  }
}
