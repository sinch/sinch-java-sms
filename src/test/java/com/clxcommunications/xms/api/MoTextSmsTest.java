package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;

public class MoTextSmsTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJson() throws Exception {
		String smsId = TestUtils.freshSmsId();
		OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
		String receivedAtString = json.writeValueAsString(receivedAt);
		String body = "Здравей, свят!";

		MoSms input = ImmutableMoTextSms.builder()
		        .to("12345")
		        .from("987654321")
		        .body(body)
		        .id(smsId)
		        .keyword("KWD")
		        .receivedAt(receivedAt)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  \"type\": \"mo_text\",",
		        "  \"to\": \"12345\",",
		        "  \"from\": \"987654321\",",
		        "  \"id\": \"" + smsId + "\",",
		        "  \"received_at\": " + receivedAtString + ",",
		        "  \"body\": \"Здравей, свят!\",",
		        "  \"keyword\": \"KWD\"",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJson() throws Exception {
		String smsId = TestUtils.freshSmsId();
		OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());

		MoSms expected = ImmutableMoTextSms.builder()
		        .to("12345")
		        .from("987654321")
		        .body("Hello, world!")
		        .id(smsId)
		        .receivedAt(receivedAt)
		        .build();

		String input = json.writeValueAsString(expected);

		MoSms actual = json.readValue(input, MoSms.class);

		assertThat(actual, is(expected));
	}

}
