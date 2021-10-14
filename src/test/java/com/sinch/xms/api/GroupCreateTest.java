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

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.sinch.testsupport.TestUtils;
import com.sinch.xms.ApiObjectMapper;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.Utils;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class GroupCreateTest {

  private final ApiObjectMapper json = new ApiObjectMapper();

  @Test
  public void canSerializeMinimal() throws Exception {
    GroupCreate input = SinchSMSApi.groupCreate().build();

    String expected = "{}";
    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canSerializeMaximalish() throws Exception {
    GroupId groupId1 = TestUtils.freshGroupId();
    GroupId groupId2 = TestUtils.freshGroupId();

    GroupCreate input =
        SinchSMSApi.groupCreate()
            .name("mygroup")
            .addMember("123456789")
            .addMember("987654321", "4242424242")
            .childGroups(Arrays.asList(groupId1, groupId2))
            .addTag("tag1", "tag2")
            .autoUpdate(
                SinchSMSApi.autoUpdate()
                    .recipient("1111")
                    .add("kw0", "kw1")
                    .remove("kw2", "kw3")
                    .build())
            .build();

    String expected =
        Utils.join(
                "\n",
                "{",
                "  'name': 'mygroup',",
                "  'members': [ '123456789', '987654321', '4242424242' ],",
                "  'child_groups': [ '" + groupId1 + "', '" + groupId2 + "' ],",
                "  'auto_update': {",
                "    'to': '1111',",
                "    'add': { 'first_word': 'kw0', 'second_word': 'kw1' },",
                "    'remove': { 'first_word': 'kw2', 'second_word': 'kw3' }",
                "  },",
                "  'tags': [ 'tag1', 'tag2' ]",
                "}")
            .replace('\'', '"');

    String actual = json.writeValueAsString(input);

    assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
  }

  @Test
  public void canDeserializeMinimal() throws Exception {
    GroupCreate expected = SinchSMSApi.groupCreate().build();

    String input = json.writeValueAsString(expected);

    GroupCreate actual = json.readValue(input, GroupCreate.class);

    assertThat(actual, is(expected));
  }

  @Test
  public void canDeserializeMaximalish() throws Exception {
    GroupId groupId1 = TestUtils.freshGroupId();
    GroupId groupId2 = TestUtils.freshGroupId();

    GroupCreate expected =
        SinchSMSApi.groupCreate()
            .name("mygroup")
            .addMember("123456789")
            .addMember("987654321", "4242424242")
            .childGroups(Arrays.asList(groupId1, groupId2))
            .addTag("tag1", "tag2")
            .autoUpdate(
                SinchSMSApi.autoUpdate()
                    .recipient("1111")
                    .add("kw0", "kw1")
                    .remove("kw2", "kw3")
                    .build())
            .build();

    String input = json.writeValueAsString(expected);

    GroupCreate actual = json.readValue(input, GroupCreate.class);

    assertThat(actual, is(expected));
  }
}
