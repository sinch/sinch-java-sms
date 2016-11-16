package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MoBinarySmsTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJson() throws Exception {
		String smsId = TestUtils.freshSmsId();
		OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
		String receivedAtString = json.writeValueAsString(receivedAt);
		byte[] body = TestUtils.UTF_8.encode("Здравей, свят!").array();
		byte[] udh = new byte[] { 0, 1, 2, 3 };

		MoSms input = MoBinarySmsImpl.builder()
		        .to("12345")
		        .from("987654321")
		        .body(body)
		        .udh(udh)
		        .id(smsId)
		        .receivedAt(receivedAt)
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  \"type\": \"mo_binary\",",
		        "  \"to\": \"12345\",",
		        "  \"from\": \"987654321\",",
		        "  \"id\": \"" + smsId + "\",",
		        "  \"received_at\": " + receivedAtString + ",",
		        "  \"body\": \"0JfQtNGA0LDQstC10LksINGB0LLRj9GCIQAAAAAAAA==\",",
		        "  \"udh\": \"00010203\"",
		        "}");

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test(expected = JsonMappingException.class)
	public void throwsWhenGettingNonHexUdh() throws Exception {
		String smsId = TestUtils.freshSmsId();
		OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
		String receivedAtString = json.writeValueAsString(receivedAt);

		String input = Utils.join("\n",
		        "{",
		        "  \"type\": \"mo_binary\",",
		        "  \"to\": \"12345\",",
		        "  \"from\": \"987654321\",",
		        "  \"id\": \"" + smsId + "\",",
		        "  \"received_at\": " + receivedAtString + ",",
		        "  \"body\": \"0JfQtNGA0LDQstC10LksINGB0LLRj9GCIQAAAAAAAA==\",",
		        "  \"udh\": \"blahblah\"",
		        "}");

		json.readValue(input, MoBinarySms.class);
	}

	@Test
	public void canDeserializeJson() throws Exception {
		String smsId = TestUtils.freshSmsId();
		OffsetDateTime receivedAt = OffsetDateTime.now(Clock.systemUTC());
		byte[] body = new byte[] { 10, 20, 30, 40, 50, 60, 70, 80, 90 };
		byte[] udh = new byte[] { 0, 1, 2, 3 };

		MoSms expected = MoBinarySmsImpl.builder()
		        .to("12345")
		        .from("987654321")
		        .body(body)
		        .udh(udh)
		        .id(smsId)
		        .receivedAt(receivedAt)
		        .build();

		String input = json.writeValueAsString(expected);

		MoSms actual = json.readValue(input, MoSms.class);

		assertThat(actual, is(expected));
	}

}
