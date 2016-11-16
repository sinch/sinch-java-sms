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
public class TagsUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Property
	public void canSerializeJson(List<String> toAdd, List<String> toRemove)
	        throws Exception {
		TagsUpdate input = TagsUpdateImpl.builder()
		        .newTag(toAdd)
		        .removeTag(toRemove)
		        .build();

		List<String> escapedToAdd = new ArrayList<String>();
		for (String tag : toAdd) {
			escapedToAdd.add("\"" + StringEscapeUtils.escapeJson(tag) + "\"");
		}

		List<String> escapedToRemove = new ArrayList<String>();
		for (String tag : toRemove) {
			escapedToRemove
			        .add("\"" + StringEscapeUtils.escapeJson(tag) + "\"");
		}

		String expected = Utils.join("\n",
		        "{",
		        "  \"to_add\" : [" + Utils.join(",", escapedToAdd) + "],",
		        "  \"to_remove\" : [" + Utils.join(",", escapedToRemove) + "]",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Property
	public void canDeserializeJson(List<String> toAdd, List<String> toRemove)
	        throws Exception {
		TagsUpdate expected = TagsUpdateImpl.builder()
		        .newTag(toAdd)
		        .removeTag(toRemove)
		        .build();

		String input = json.writeValueAsString(expected);

		TagsUpdate actual = json.readValue(input, TagsUpdate.class);

		assertThat(actual, is(expected));
	}

}
