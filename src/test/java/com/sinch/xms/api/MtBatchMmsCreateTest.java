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
                "    'message':'Hello, world!',",
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
  public void canSerializeWithParameters() throws Exception {
    MtBatchMmsCreate input =
        minimalBatchBuilder()
            .putParameter(
                "param1",
                SinchSMSApi.parameterValues()
                    .putSubstitution("123", "foo")
                    .putSubstitution("234", "bar")
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
                "    'message':'Hello, world!',",
                "    'url':'http://my.test.url/image.jpg'",
                "  },",
                "  'parameters': {",
                "    'param1': {",
                "      '123': 'foo',",
                "      '234': 'bar'",
                "    }",
                "  }",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeWithoutMessageContent() throws Exception {
    MtBatchMmsCreate input =
        SinchSMSApi.batchMms()
            .sender("1234")
            .addRecipient("987654321")
            .body(MediaBody.builder().url("http://my.test.url/image.jpg").build())
            .build();

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
  public void canDeserializeWithoutMessageContent() throws Exception {
    MtBatchMmsCreate expected =
        SinchSMSApi.batchMms()
            .sender("1234")
            .addRecipient("987654321")
            .body(MediaBody.builder().url("http://my.test.url/image.jpg").build())
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
        .body(MediaBody.builder().message("Hello, world!").build())
        .build();
  }

  private static MtBatchMmsCreate.Builder minimalBatchBuilder() {
    return SinchSMSApi.batchMms()
        .sender("1234")
        .addRecipient("987654321")
        .body(
            MediaBody.builder()
                .message("Hello, world!")
                .url("http://my.test.url/image.jpg")
                .build());
  }
}
