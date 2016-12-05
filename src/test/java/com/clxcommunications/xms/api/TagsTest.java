package com.clxcommunications.xms.api;

/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.runner.RunWith;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class TagsTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Property
	public void canIterateOverTags(List<String> tags) throws Exception {
		for (String t : Tags.of(tags)) {
			assertThat(t, is(tags.remove(0)));
		}
	}

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
