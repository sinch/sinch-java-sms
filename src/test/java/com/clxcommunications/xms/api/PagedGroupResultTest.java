package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class PagedGroupResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJsonWithEmptyBatches() throws Exception {
		PagedGroupResult input =
		        new PagedGroupResult.Builder()
		                .page(0)
		                .size(0)
		                .numPages(0)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 0,",
		        "  'count' : 0,",
		        "  'groups' : []",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithEmptyBatches() throws Exception {
		PagedGroupResult expected =
		        new PagedGroupResult.Builder()
		                .page(0)
		                .size(0)
		                .numPages(0)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedGroupResult actual =
		        json.readValue(input, PagedGroupResult.class);

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

		GroupResponse groupResult1 =
		        new GroupResponse.Builder()
		                .size(72)
		                .id(groupId1)
		                .createdAt(time1)
		                .modifiedAt(time2)
		                .name("groupname")
		                .build();

		GroupResponse groupResult2 =
		        new GroupResponse.Builder()
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
		                .numPages(0)
		                .addContent(groupResult1)
		                .addContent(groupResult2)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 1,",
		        "  'count' : 0,",
		        "  'groups' : [",
		        "    {",
		        "      'id': '" + groupId1.id() + "',",
		        "      'name': 'groupname',",
		        "      'size': 72,",
		        "      'created_at': " + timeString1 + ",",
		        "      'modified_at': " + timeString2 + "",
		        "    },",
		        "    {",
		        "      'id': '" + groupId2.id() + "',",
		        "      'size': 20,",
		        "      'created_at': " + timeString2 + ",",
		        "      'modified_at': " + timeString1 + ",",
		        "      'child_groups': [ '" + groupId1.id() + "' ]",
		        "    }",
		        "  ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithNonEmptyBatches() throws Exception {
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		GroupResponse groupResult1 =
		        new GroupResponse.Builder()
		                .size(72)
		                .id(groupId1)
		                .createdAt(time)
		                .modifiedAt(time)
		                .name("groupname")
		                .build();

		GroupResponse groupResult2 =
		        new GroupResponse.Builder()
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
		                .numPages(0)
		                .addContent(groupResult1)
		                .addContent(groupResult2)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedGroupResult actual =
		        json.readValue(input, PagedGroupResult.class);

		assertThat(actual, is(expected));
	}

}
