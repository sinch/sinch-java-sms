package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class GroupMembersTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerialize() throws Exception {
		Iterable<String> members = Arrays.asList("123", "345", "567");
		GroupMembers input = GroupMembers.of(members);

		String expected = Utils.join("\n",
		        "{",
		        "  'members': [ '123', '345', '567' ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Property
	public void canDeserialize(List<String> members) throws Exception {
		GroupMembers expected = GroupMembers.of(members);

		String input = json.writeValueAsString(expected);

		GroupMembers actual = json.readValue(input, GroupMembers.class);

		assertThat(actual, is(expected));
	}

}
