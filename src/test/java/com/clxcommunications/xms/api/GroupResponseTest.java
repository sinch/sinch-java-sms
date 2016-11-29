package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class GroupResponseTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		GroupId groupId = TestUtils.freshGroupId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		GroupResponse input = new GroupResponse.Builder()
		        .size(72)
		        .id(groupId)
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'id': '" + groupId + "',",
		        "  'size': 72,",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeMaximalish() throws Exception {
		GroupId groupId = TestUtils.freshGroupId();
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();

		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		GroupResponse input = new GroupResponse.Builder()
		        .id(groupId)
		        .size(72)
		        .name("mygroup")
		        .childGroups(Arrays.asList(groupId1, groupId2))
		        .autoUpdate(AutoUpdate.builder()
		                .to("1111")
		                .add("kw0", "kw1")
		                .remove("kw2", "kw3")
		                .build())
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'id': '" + groupId + "',",
		        "  'name': 'mygroup',",
		        "  'size': 72,",
		        "  'child_groups': [ '" + groupId1 + "', '" + groupId2 + "' ],",
		        "  'auto_update': {",
		        "    'to': '1111',",
		        "    'add': { 'first_word': 'kw0', 'second_word': 'kw1' },",
		        "    'remove': { 'first_word': 'kw2', 'second_word': 'kw3' }",
		        "  },",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		GroupId groupId = TestUtils.freshGroupId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		GroupResponse expected = new GroupResponse.Builder()
		        .size(72)
		        .id(groupId)
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String input = json.writeValueAsString(expected);

		GroupResponse actual = json.readValue(input, GroupResponse.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canDeserializeMaximalish() throws Exception {
		GroupId groupId = TestUtils.freshGroupId();
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		GroupResponse expected = new GroupResponse.Builder()
		        .id(groupId)
		        .name("mygroup")
		        .size(72)
		        .childGroups(Arrays.asList(groupId1, groupId2))
		        .autoUpdate(AutoUpdate.builder()
		                .to("1111")
		                .add("kw0", "kw1")
		                .remove("kw2", "kw3")
		                .build())
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String input = json.writeValueAsString(expected);

		GroupResponse actual = json.readValue(input, GroupResponse.class);

		assertThat(actual, is(expected));
	}

}
