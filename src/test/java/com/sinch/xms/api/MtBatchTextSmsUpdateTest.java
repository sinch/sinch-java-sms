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
import com.sinch.xms.UpdateValue;
import com.sinch.xms.Utils;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

public class MtBatchTextSmsUpdateTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeMinimal() throws Exception {
    MtBatchSmsUpdate input = SinchSMSApi.batchTextSmsUpdate().build();

    String expected = Utils.join("\n", "{", "  'type': 'mt_text'", "}").replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeWithUpdatedParameters() throws Exception {
    Map<String, ParameterValues> params = new TreeMap<String, ParameterValues>();

    params.put("newparam", SinchSMSApi.parameterValues().putSubstitution("key1", "value1").build());

    MtBatchSmsUpdate input =
        SinchSMSApi.batchTextSmsUpdate()
            .sender("1234")
            .addRecipientInsertion("987654321")
            .body("Hello, world!")
            .parameters(UpdateValue.set(params))
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'type': 'mt_text',",
                "  'from': '1234',",
                "  'to_add': [ '987654321' ],",
                "  'body': 'Hello, world!',",
                "  'parameters': { 'newparam': { 'key1': 'value1' } }",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeWithUnsetParameters() throws Exception {
    MtBatchSmsUpdate input =
        SinchSMSApi.batchTextSmsUpdate()
            .sender("1234")
            .addRecipientInsertion("987654321")
            .body("Hello, world!")
            .unsetParameters()
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'type': 'mt_text',",
                "  'from': '1234',",
                "  'to_add': [ '987654321' ],",
                "  'body': 'Hello, world!',",
                "  'parameters': null",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }
}
