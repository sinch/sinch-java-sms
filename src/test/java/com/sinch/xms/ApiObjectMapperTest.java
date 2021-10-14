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
package com.sinch.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import com.sinch.testsupport.TestUtils;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

public class ApiObjectMapperTest {

  /** Just a simple object used to verify that our Jackson configuration is OK. */
  private static class MapperTestClass {

    private OffsetDateTime time;

    public void setTime(OffsetDateTime time) {
      this.time = time;
    }

    public OffsetDateTime getTime() {
      return time;
    }
  }

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void ignoresUnknownFields() throws Exception {
    String input = Utils.join("\n", "{", "  'unknown_field': 42", "}").replace('\'', '"');

    // Should _not_ throw an exception.
    MapperTestClass result = json.readValue(input, MapperTestClass.class);

    assertThat(result, notNullValue());
    assertThat(result.getTime(), is(nullValue()));
  }

  @Test
  public void serializesThreeTenOffsetDateTime() throws Exception {
    OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    MapperTestClass input = new MapperTestClass();
    input.setTime(time);

    String expected =
        Utils.join("\n", "{", "  'time': '2016-10-02T09:34:28.542Z'", "}").replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void deserializesThreeTenOffsetDateTime() throws Exception {
    OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    String input =
        Utils.join("\n", "{", "  'time': '2016-10-02T09:34:28.542Z'", "}").replace('\'', '"');

    MapperTestClass result = json.readValue(input, MapperTestClass.class);

    assertThat(result.getTime(), is(time));
  }

  @Test
  public void testOffsetNotChangedWhenDeserialize() throws Exception {
    OffsetDateTime time =
        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.of("+01:00"));

    String input =
        Utils.join("\n", "{", "  'time': '2016-10-02T09:34:28.542+01:00'", "}").replace('\'', '"');

    MapperTestClass result = json.readValue(input, MapperTestClass.class);

    assertThat(result.getTime(), is(time));
  }

  @Test
  public void testOffsetNotChangedWhenSerialize() throws Exception {
    OffsetDateTime time =
        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.of("+01:00"));
    MapperTestClass input = new MapperTestClass();
    input.setTime(time);

    String expected =
        Utils.join("\n", "{", "  'time': '2016-10-02T09:34:28.542+01:00'", "}").replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }
}
