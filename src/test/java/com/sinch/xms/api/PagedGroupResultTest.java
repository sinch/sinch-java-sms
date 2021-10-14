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
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

public class PagedGroupResultTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeJsonWithEmptyBatches() throws Exception {
    PagedGroupResult input = new PagedGroupResult.Builder().page(0).size(0).totalSize(0).build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'page' : 0,",
                "  'page_size' : 0,",
                "  'count' : 0,",
                "  'groups' : []",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeJsonWithEmptyBatches() throws Exception {
    PagedGroupResult expected = new PagedGroupResult.Builder().page(0).size(0).totalSize(0).build();

    String input = json.writeValueAsString(expected);

    PagedGroupResult actual = json.readValue(input, PagedGroupResult.class);

    assertThat(actual, is(expected));
  }

  @Test
  public void canSerializeJsonWithNonEmptyBatches() throws Exception {
    GroupId groupId1 = TestUtils.freshGroupId();
    GroupId groupId2 = TestUtils.freshGroupId();
    OffsetDateTime time1 = OffsetDateTime.now(Clock.systemUTC());
    String timeString1 = json.writeValueAsString(time1);
    OffsetDateTime time2 = OffsetDateTime.now(Clock.systemUTC());
    String timeString2 = json.writeValueAsString(time2);

    GroupResult groupResult1 =
        new GroupResult.Builder()
            .size(72)
            .id(groupId1)
            .createdAt(time1)
            .modifiedAt(time2)
            .name("groupname")
            .build();

    GroupResult groupResult2 =
        new GroupResult.Builder()
            .size(20)
            .id(groupId2)
            .createdAt(time2)
            .modifiedAt(time1)
            .addChildGroup(groupId1)
            .build();

    PagedGroupResult input =
        new PagedGroupResult.Builder()
            .page(0)
            .size(1)
            .totalSize(0)
            .addContent(groupResult1)
            .addContent(groupResult2)
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'page' : 0,",
                "  'page_size' : 1,",
                "  'count' : 0,",
                "  'groups' : [",
                "    {",
                "      'id': '" + groupId1 + "',",
                "      'name': 'groupname',",
                "      'size': 72,",
                "      'created_at': " + timeString1 + ",",
                "      'modified_at': " + timeString2 + "",
                "    },",
                "    {",
                "      'id': '" + groupId2 + "',",
                "      'size': 20,",
                "      'created_at': " + timeString2 + ",",
                "      'modified_at': " + timeString1 + ",",
                "      'child_groups': [ '" + groupId1 + "' ]",
                "    }",
                "  ]",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeJsonWithNonEmptyBatches() throws Exception {
    GroupId groupId1 = TestUtils.freshGroupId();
    GroupId groupId2 = TestUtils.freshGroupId();
    OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

    GroupResult groupResult1 =
        new GroupResult.Builder()
            .size(72)
            .id(groupId1)
            .createdAt(time)
            .modifiedAt(time)
            .name("groupname")
            .build();

    GroupResult groupResult2 =
        new GroupResult.Builder()
            .size(20)
            .id(groupId2)
            .createdAt(time)
            .modifiedAt(time)
            .addChildGroup(groupId1)
            .build();

    PagedGroupResult expected =
        new PagedGroupResult.Builder()
            .page(0)
            .size(1)
            .totalSize(0)
            .addContent(groupResult1)
            .addContent(groupResult2)
            .build();

    String input = json.writeValueAsString(expected);

    PagedGroupResult actual = json.readValue(input, PagedGroupResult.class);

    assertThat(actual, is(expected));
  }
}
