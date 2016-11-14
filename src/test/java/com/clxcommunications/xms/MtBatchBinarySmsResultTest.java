package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MtBatchBinarySmsResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsResult input = minimalBatchBuilder().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary',",
		        "  'from': '1234',",
		        "  'to': [ '987654321' ],",
		        "  'canceled': false,",
		        "  'id': '" + input.id().id() + "',",
		        "  'body': 'SGVsbG8sIHdvcmxkIQ==',",
		        "  'udh': '01020304'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		MtBatchSmsResult expected = minimalBatchBuilder().build();

		String input = json.writeValueAsString(expected);

		MtBatchSmsResult actual = json.readValue(input, MtBatchSmsResult.class);

		assertThat(actual, is(expected));
	}

	private static ImmutableMtBatchBinarySmsResult.Builder minimalBatchBuilder() {
		return ImmutableMtBatchBinarySmsResult.builder()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!".getBytes(TestUtils.US_ASCII))
		        .udh(new byte[] { 1, 2, 3, 4 })
		        .canceled(false)
		        .id(TestUtils.freshBatchId());
	}

}
