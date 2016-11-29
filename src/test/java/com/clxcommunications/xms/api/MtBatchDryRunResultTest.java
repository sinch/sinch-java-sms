package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class MtBatchDryRunResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeMinimal() throws Exception {
		MtBatchDryRunResult input =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'number_of_recipients': 20,",
		        "  'number_of_messages': 200",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeMinimal() throws Exception {
		MtBatchDryRunResult expected =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .build();

		String input = json.writeValueAsString(expected);

		MtBatchDryRunResult actual =
		        json.readValue(input, MtBatchDryRunResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeWithPerRecipient() throws Exception {
		MtBatchDryRunResult input =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .addPerRecipient(
		                        new MtBatchDryRunResult.PerRecipient.Builder()
		                                .recipient("123456789")
		                                .numberOfParts(3)
		                                .body("body1")
		                                .encoding("encoding1")
		                                .build())
		                .addPerRecipient(
		                        new MtBatchDryRunResult.PerRecipient.Builder()
		                                .recipient("987654321")
		                                .numberOfParts(2)
		                                .body("body2")
		                                .encoding("encoding2")
		                                .build())
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'number_of_recipients': 20,",
		        "  'number_of_messages': 200,",
		        "  'per_recipient': [",
		        "    {",
		        "      'recipient': '123456789',",
		        "      'number_of_parts': 3,",
		        "      'body': 'body1',",
		        "      'encoding': 'encoding1'",
		        "    },",
		        "    {",
		        "      'recipient': '987654321',",
		        "      'number_of_parts': 2,",
		        "      'body': 'body2',",
		        "      'encoding': 'encoding2'",
		        "    }",
		        "   ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeWithPerRecipient() throws Exception {
		MtBatchDryRunResult expected =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .addPerRecipient(
		                        new MtBatchDryRunResult.PerRecipient.Builder()
		                                .recipient("123456789")
		                                .numberOfParts(3)
		                                .body("body1")
		                                .encoding("encoding1")
		                                .build())
		                .addPerRecipient(
		                        new MtBatchDryRunResult.PerRecipient.Builder()
		                                .recipient("987654321")
		                                .numberOfParts(2)
		                                .body("body2")
		                                .encoding("encoding2")
		                                .build())
		                .build();

		String input = json.writeValueAsString(expected);

		MtBatchDryRunResult actual =
		        json.readValue(input, MtBatchDryRunResult.class);

		assertThat(actual, is(expected));
	}

}
