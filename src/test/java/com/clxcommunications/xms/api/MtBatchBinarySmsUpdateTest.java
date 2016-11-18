package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.UpdateValue;
import com.clxcommunications.xms.Utils;

public class MtBatchBinarySmsUpdateTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchSmsUpdate input = ClxApi.buildBatchBinarySmsUpdate().build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canSerializeWithUpdatedFields() throws Exception {
		MtBatchSmsUpdate input = ClxApi.buildBatchBinarySmsUpdate()
		        .from("1234")
		        .addToAdd("987654321")
		        .body("Hello, world!".getBytes(TestUtils.US_ASCII))
		        .udh(new byte[] { 1, 2, 3, 4 })
		        .deliveryReport(UpdateValue.<ReportType> unset())
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'type': 'mt_binary',",
		        "  'from': '1234',",
		        "  'to_add': [ '987654321' ],",
		        "  'body': 'SGVsbG8sIHdvcmxkIQ==',",
		        "  'udh': '01020304',",
		        "  'delivery_report': null",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

}
