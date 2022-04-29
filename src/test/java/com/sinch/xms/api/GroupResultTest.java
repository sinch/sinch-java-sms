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
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import org.junit.Test;

public class GroupResultTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeMinimal() throws Exception {
    GroupId groupId = TestUtils.freshGroupId();
    OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    GroupResult input =
        new GroupResult.Builder().size(72).id(groupId).createdAt(time).modifiedAt(time).build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'id': '" + groupId + "',",
                "  'size': 72,",
                "  'created_at': '2016-10-02T09:34:28.542Z',",
                "  'modified_at': '2016-10-02T09:34:28.542Z'",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeMaximalish() throws Exception {
    GroupId groupId = TestUtils.freshGroupId();
    GroupId gid1 = TestUtils.freshGroupId();
    GroupId gid2 = TestUtils.freshGroupId();

    OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000, ZoneOffset.UTC);

    GroupResult input =
        new GroupResult.Builder()
            .id(groupId)
            .size(72)
            .name("mygroup")
            .childGroups(Arrays.asList(gid1, gid2))
            .autoUpdate(
                SinchSMSApi.autoUpdate()
                    .recipient("1111")
                    .add("kw0", "kw1")
                    .remove("kw2", "kw3")
                    .build())
            .createdAt(time)
            .modifiedAt(time)
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'id': '" + groupId + "',",
                "  'name': 'mygroup',",
                "  'size': 72,",
                "  'child_groups': [ '" + gid1 + "', '" + gid2 + "' ],",
                "  'auto_update': {",
                "    'to': '1111',",
                "    'add': { 'first_word': 'kw0', 'second_word': 'kw1' },",
                "    'remove': { 'first_word': 'kw2', 'second_word': 'kw3' }",
                "  },",
                "  'created_at': '2016-10-02T09:34:28.542Z',",
                "  'modified_at': '2016-10-02T09:34:28.542Z'",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeMinimal() throws Exception {
    GroupId groupId = TestUtils.freshGroupId();
    OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

    GroupResult expected =
        new GroupResult.Builder().size(72).id(groupId).createdAt(time).modifiedAt(time).build();

    String input = json.writeValueAsString(expected);

    GroupResult actual = json.readValue(input, GroupResult.class);

    assertThat(actual, is(expected));
  }

  @Test
  public void canDeserializeMaximalish() throws Exception {
    GroupId groupId = TestUtils.freshGroupId();
    GroupId groupId1 = TestUtils.freshGroupId();
    GroupId groupId2 = TestUtils.freshGroupId();
    OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

    GroupResult expected =
        new GroupResult.Builder()
            .id(groupId)
            .name("mygroup")
            .size(72)
            .childGroups(Arrays.asList(groupId1, groupId2))
            .autoUpdate(
                SinchSMSApi.autoUpdate()
                    .recipient("1111")
                    .add("kw0", "kw1")
                    .remove("kw2", "kw3")
                    .build())
            .createdAt(time)
            .modifiedAt(time)
            .build();

    String input = json.writeValueAsString(expected);

    GroupResult actual = json.readValue(input, GroupResult.class);

    assertThat(actual, is(expected));
  }
}
