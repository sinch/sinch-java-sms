package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class GroupUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		GroupUpdate input = GroupUpdate.builder().build();

		String expected = "{}";
		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeMaximalish() throws Exception {
		GroupId groupId1 = TestUtils.freshGroupId();
		GroupId groupId2 = TestUtils.freshGroupId();
		GroupId groupId3 = TestUtils.freshGroupId();

		GroupUpdate input = GroupUpdate.builder()
		        .unsetName()
		        .addMemberAdd("123456789")
		        .addMemberRemove("987654321", "4242424242")
		        .childGroupsAdd(Arrays.asList(groupId1, groupId2))
		        .addChildGroupsRemove(groupId3)
		        .autoUpdate(AutoUpdate.builder()
		                .to("1111")
		                .add("kw0", "kw1")
		                .remove("kw2", "kw3")
		                .build())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'name': null,",
		        "  'add': [ '123456789' ],",
		        "  'remove': [ '987654321', '4242424242' ],",
		        "  'child_groups_add': [ '" + groupId1.id() + "', '"
		                + groupId2.id() + "' ],",
		        "  'child_groups_remove': [ '" + groupId3.id() + "' ],",
		        "  'auto_update': {",
		        "    'to': '1111',",
		        "    'add': { 'first_word': 'kw0', 'second_word': 'kw1' },",
		        "    'remove': { 'first_word': 'kw2', 'second_word': 'kw3' }",
		        "  }",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

}
