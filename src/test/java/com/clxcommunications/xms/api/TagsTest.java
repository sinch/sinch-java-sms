package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.runner.RunWith;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class TagsTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Property
	public void canSerializeJson(List<String> tags) throws Exception {
		Tags input = TagsImpl.builder().tags(tags).build();

		List<String> escapedTags = new ArrayList<String>();
		for (String tag : tags) {
			escapedTags.add("\"" + StringEscapeUtils.escapeJson(tag) + "\"");
		}

		String expected = Utils.join("\n",
		        "{",
		        "  \"tags\" : [" + Utils.join(",", escapedTags) + "]",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Property
	public void canDeserializeJson(List<String> tags) throws Exception {
		Tags expected = TagsImpl.builder().tags(tags).build();

		String input = json.writeValueAsString(expected);

		Tags actual = json.readValue(input, Tags.class);

		assertThat(actual, is(expected));
	}

}
