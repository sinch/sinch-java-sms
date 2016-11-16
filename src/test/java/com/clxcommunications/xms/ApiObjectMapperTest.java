package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

public class ApiObjectMapperTest {

	/**
	 * Just a simple object used to verify that our Jackson configuration is OK.
	 */
	private static class MapperTestClass {

		private OffsetDateTime time;

		public void setTime(OffsetDateTime time) {
			this.time = time;
		}

		public OffsetDateTime getTime() {
			return time;
		}

	}

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void ignoresUnknownFields() throws Exception {
		String input = Utils.join("\n",
		        "{",
		        "  'unknown_field': 42",
		        "}").replace('\'', '"');

		// Should _not_ throw an exception.
		MapperTestClass result = json.readValue(input, MapperTestClass.class);

		assertThat(result, notNullValue());
		assertThat(result.getTime(), is(nullValue()));
	}

	@Test
	public void serializesThreeTenOffsetDateTime() throws Exception {
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MapperTestClass input = new MapperTestClass();
		input.setTime(time);

		String expected = Utils.join("\n",
		        "{",
		        "  'time': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void deserializesThreeTenOffsetDateTime() throws Exception {
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		String input = Utils.join("\n",
		        "{",
		        "  'time': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		MapperTestClass result = json.readValue(input, MapperTestClass.class);

		assertThat(result.getTime(), is(time));
	}

}
