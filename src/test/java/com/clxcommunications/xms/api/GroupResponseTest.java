package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;

public class GroupResponseTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		GroupResponse input = GroupResponseImpl.builder()
		        .size(72)
		        .id("groupid")
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'id': 'groupid',",
		        "  'size': 72,",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeMaximalish() throws Exception {
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		GroupResponse input = GroupResponseImpl.builder()
		        .id("groupid")
		        .size(72)
		        .name("mygroup")
		        .childGroups(Arrays.asList("group1", "group2"))
		        .autoUpdate(AutoUpdateImpl.builder()
		                .to("1111")
		                .addKeywordFirst("kw0")
		                .addKeywordSecond("kw1")
		                .removeKeywordFirst("kw2")
		                .removeKeywordSecond("kw3")
		                .build())
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'id': 'groupid',",
		        "  'name': 'mygroup',",
		        "  'size': 72,",
		        "  'child_groups': [ 'group1', 'group2' ],",
		        "  'auto_update': {",
		        "    'to': '1111',",
		        "    'add_keyword_first': 'kw0',",
		        "    'add_keyword_second': 'kw1',",
		        "    'remove_keyword_first': 'kw2',",
		        "    'remove_keyword_second': 'kw3'",
		        "  },",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		GroupResponse expected = GroupResponseImpl.builder()
		        .size(72)
		        .id("groupid")
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String input = json.writeValueAsString(expected);

		GroupResponse actual = json.readValue(input, GroupResponse.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canDeserializeMaximalish() throws Exception {
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		GroupResponse expected = GroupResponseImpl.builder()
		        .id("groupid")
		        .name("mygroup")
		        .size(72)
		        .childGroups(Arrays.asList("group1", "group2"))
		        .autoUpdate(AutoUpdateImpl.builder()
		                .to("1111")
		                .addKeywordFirst("kw0")
		                .addKeywordSecond("kw1")
		                .removeKeywordFirst("kw2")
		                .removeKeywordSecond("kw3")
		                .build())
		        .createdAt(time)
		        .modifiedAt(time)
		        .build();

		String input = json.writeValueAsString(expected);

		GroupResponse actual = json.readValue(input, GroupResponse.class);

		assertThat(actual, is(expected));
	}

}
