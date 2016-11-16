package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.ApiObjectMapper;
import com.clxcommunications.xms.TestUtils;
import com.clxcommunications.xms.Utils;

public class PagedBatchResultTest {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Test
	public void canSerializeJsonWithEmptyBatches() throws Exception {
		PagedBatchResult input =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(0)
		                .numPages(0)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 0,",
		        "  'count' : 0,",
		        "  'batches' : []",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithEmptyBatches() throws Exception {
		PagedBatchResult expected =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(0)
		                .numPages(0)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedBatchResult actual =
		        json.readValue(input, PagedBatchResult.class);

		assertThat(actual, is(expected));
	}

	@Test
	public void canSerializeJsonWithNonEmptyBatches() throws Exception {
		BatchId batchId1 = TestUtils.freshBatchId();
		BatchId batchId2 = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.now(Clock.systemUTC());
		String smsTimeString = json.writeValueAsString(smsTime);

		MtBatchSmsResult batchResult1 =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId1)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		MtBatchSmsResult batchResult2 =
		        ImmutableMtBatchBinarySmsResult.builder()
		                .using(batchResult1)
		                .id(batchId2)
		                .body("foobar".getBytes(TestUtils.US_ASCII))
		                .udh(new byte[] { 1, 2, 3 })
		                .build();

		PagedBatchResult input =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(1)
		                .numPages(0)
		                .addBatch(batchResult1)
		                .addBatch(batchResult2)
		                .build();

		String expected = Utils.join("\n",
		        "{",
		        "  'page' : 0,",
		        "  'page_size' : 1,",
		        "  'count' : 0,",
		        "  'batches' : [",
		        "    {",
		        "      'type' : 'mt_text',",
		        "      'to': [",
		        "        '123456789',",
		        "        '987654321'",
		        "      ],",
		        "      'body': 'Hello, world!',",
		        "      'canceled': false,",
		        "      'id': '" + batchId1.id() + "',",
		        "      'from': '12345',",
		        "      'created_at': " + smsTimeString + ",",
		        "      'modified_at': " + smsTimeString + "",
		        "    },",
		        "    {",
		        "      'type' : 'mt_binary',",
		        "      'to': [",
		        "        '123456789',",
		        "        '987654321'",
		        "      ],",
		        "      'body': 'Zm9vYmFy',",
		        "      'udh': '010203',",
		        "      'canceled': false,",
		        "      'id': '" + batchId2.id() + "',",
		        "      'from': '12345',",
		        "      'created_at': " + smsTimeString + ",",
		        "      'modified_at': " + smsTimeString + "",
		        "    }",
		        "  ]",
		        "}").replace('\'', '"');

		String actual = json.writeValueAsString(input);

		assertThat(actual, is(TestUtils.jsonEqualTo(expected)));
	}

	@Test
	public void canDeserializeJsonWithNonEmptyBatches() throws Exception {
		BatchId batchId1 = TestUtils.freshBatchId();
		BatchId batchId2 = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.now(Clock.systemUTC());

		MtBatchTextSmsResult batchResult1 =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId1)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		MtBatchTextSmsResult batchResult2 =
		        ImmutableMtBatchTextSmsResult.builder()
		                .using(batchResult1)
		                .id(batchId2)
		                .body("Hello, again!")
		                .build();

		PagedBatchResult expected =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(1)
		                .numPages(0)
		                .addBatch(batchResult1)
		                .addBatch(batchResult2)
		                .build();

		String input = json.writeValueAsString(expected);

		PagedBatchResult actual =
		        json.readValue(input, PagedBatchResult.class);

		assertThat(actual, is(expected));
	}

}
