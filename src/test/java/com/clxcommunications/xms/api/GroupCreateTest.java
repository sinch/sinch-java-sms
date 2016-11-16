package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class GroupCreateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		GroupCreate input = GroupCreateImpl.builder().build();

		String expected = "{}";
		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeMaximalish() throws Exception {
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();

		GroupCreate input = GroupCreateImpl.builder()
		        .name("mygroup")
		        .addMember("123456789")
		        .addMember("987654321", "4242424242")
		        .childGroups(Arrays.asList(groupId1, groupId2))
		        .addTag("tag1", "tag2")
		        .autoUpdate(AutoUpdateImpl.builder()
		                .to("1111")
		                .addKeywordFirst("kw0")
		                .addKeywordSecond("kw1")
		                .removeKeywordFirst("kw2")
		                .removeKeywordSecond("kw3")
		                .build())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'name': 'mygroup',",
		        "  'members': [ '123456789', '987654321', '4242424242' ],",
		        "  'child_groups': [ '" + groupId1.id() + "', '" + groupId2.id()
		                + "' ],",
		        "  'auto_update': {",
		        "    'to': '1111',",
		        "    'add_keyword_first': 'kw0',",
		        "    'add_keyword_second': 'kw1',",
		        "    'remove_keyword_first': 'kw2',",
		        "    'remove_keyword_second': 'kw3'",
		        "  },",
		        "  'tags': [ 'tag1', 'tag2' ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		GroupCreate expected = GroupCreateImpl.builder().build();

		String input = json.writeValueAsString(expected);

		GroupCreate actual = json.readValue(input, GroupCreate.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canDeserializeMaximalish() throws Exception {
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();

		GroupCreate expected = GroupCreateImpl.builder()
		        .name("mygroup")
		        .addMember("123456789")
		        .addMember("987654321", "4242424242")
		        .childGroups(Arrays.asList(groupId1, groupId2))
		        .addTag("tag1", "tag2")
		        .autoUpdate(AutoUpdateImpl.builder()
		                .to("1111")
		                .addKeywordFirst("kw0")
		                .addKeywordSecond("kw1")
		                .removeKeywordFirst("kw2")
		                .removeKeywordSecond("kw3")
		                .build())
		        .build();

		String input = json.writeValueAsString(expected);

		GroupCreate actual = json.readValue(input, GroupCreate.class);

		assertThat(actual, is(expected));
	}

}
