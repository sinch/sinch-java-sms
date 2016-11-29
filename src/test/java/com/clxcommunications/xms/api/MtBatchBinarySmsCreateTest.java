package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.Utils;

public class MtBatchBinarySmsCreateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsCreate input = minimalBatchBuilder().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'body': 'SGVsbG8sIHdvcmxkIQ==',",
		        "  'udh': '01020304'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		MtBatchSmsCreate expected = minimalBatchBuilder().build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsCreate actual = json.readValue(input, MtBatchSmsCreate.class);

		assertThat(actual, is(expected));
	}

	private static MtBatchBinarySmsCreate.Builder minimalBatchBuilder() {
		return ClxApi.buildBatchBinarySms()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!".getBytes(TestUtils.US_ASCII))
		        .udh(new byte[] { 1, 2, 3, 4 });
	}

}
