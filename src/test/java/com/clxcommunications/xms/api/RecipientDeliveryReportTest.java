package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.Utils;

public class RecipientDeliveryReportTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerialize() throws Exception {
		OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		RecipientDeliveryReport input = RecipientDeliveryReportImpl.builder()
		        .batchId(BatchId.of("batchid"))
		        .recipient("12345")
		        .code(10)
		        .status(DeliveryStatus.DELIVERED)
		        .statusMessage("status message")
		        .at(time1)
		        .operatorStatusAt(time2)
		        .operator("818181")
		        .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'batch_id': 'batchid',",
		        "  'recipient': '12345',",
		        "  'type': 'recipient_delivery_report_sms',",
		        "  'code': 10,",
		        "  'status': 'Delivered',",
		        "  'status_message': 'status message',",
		        "  'operator': '818181',",
		        "  'at': '2016-10-02T09:34:28.542Z',",
		        "  'operator_status_at': '2016-11-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserialize() throws Exception {
		OffsetDateTime time1 = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		OffsetDateTime time2 = OffsetDateTime.of(2016, 11, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		RecipientDeliveryReport expected = RecipientDeliveryReportImpl.builder()
		        .batchId(BatchId.of("batchid"))
		        .recipient("1235")
		        .code(10)
		        .status(DeliveryStatus.DELIVERED)
		        .statusMessage("status message")
		        .at(time1)
		        .operatorStatusAt(time2)
		        .build();

		String input = json.writeValueAsString(expected);

		RecipientDeliveryReport actual =
		        json.readValue(input, RecipientDeliveryReport.class);

		assertThat(actual, is(expected));
	}

}
