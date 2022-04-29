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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.Utils;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.junit.Test;

public class MoBinarySmsTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeJson() throws Exception {
    String smsId = TestUtils.freshSmsId();
    OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
    String receivedAtString = json.writeValueAsString(receivedAt);
    byte[] body = TestUtils.UTF_8.encode("Здравей, свят!").array();
    byte[] udh = new byte[] {0, 1, 2, 3};

    MoSms input =
        new MoBinarySms.Builder()
            .recipient("12345")
            .sender("987654321")
            .body(body)
            .udh(udh)
            .id(smsId)
            .receivedAt(receivedAt)
            .build();

    String expected =
        Utils.join(
            "\n",
            "{",
            "  \"type\": \"mo_binary\",",
            "  \"to\": \"12345\",",
            "  \"from\": \"987654321\",",
            "  \"id\": \"" + smsId + "\",",
            "  \"received_at\": " + receivedAtString + ",",
            "  \"body\": \"0JfQtNGA0LDQstC10LksINGB0LLRj9GCIQAAAAAAAA==\",",
            "  \"udh\": \"00010203\"",
            "}");

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test(expected = JsonMappingException.class)
  public void throwsWhenGettingNonHexUdh() throws Exception {
    String smsId = TestUtils.freshSmsId();
    OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
    String receivedAtString = json.writeValueAsString(receivedAt);

    String input =
        Utils.join(
            "\n",
            "{",
            "  \"type\": \"mo_binary\",",
            "  \"to\": \"12345\",",
            "  \"from\": \"987654321\",",
            "  \"id\": \"" + smsId + "\",",
            "  \"received_at\": " + receivedAtString + ",",
            "  \"body\": \"0JfQtNGA0LDQstC10LksINGB0LLRj9GCIQAAAAAAAA==\",",
            "  \"udh\": \"blahblah\"",
            "}");

    json.readValue(input, MoBinarySms.class);
  }

  @Test
  public void canDeserializeJson() throws Exception {
    String smsId = TestUtils.freshSmsId();
    OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
    byte[] body = new byte[] {10, 20, 30, 40, 50, 60, 70, 80, 90};
    byte[] udh = new byte[] {0, 1, 2, 3};

    MoSms expected =
        new MoBinarySms.Builder()
            .recipient("12345")
            .sender("987654321")
            .body(body)
            .udh(udh)
            .id(smsId)
            .receivedAt(receivedAt)
            .build();

    String input = json.writeValueAsString(expected);

    MoSms actual = json.readValue(input, MoSms.class);

    assertThat(actual, is(expected));
  }
}
