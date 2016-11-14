package com.clxcommunications.xms;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.http.concurrent.FutureCallback;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ApiConnectionIT {

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Rule
	public WireMockRule wm = new WireMockRule(
	        WireMockConfiguration.options()
	                .dynamicPort()
	                .dynamicHttpsPort());

	@Test
	public void canPostSimpleBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime =
		        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000,
		                ZoneOffset.UTC);

		MtBatchTextSms sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		String expectedRequest = json.writeValueAsString(sms);

		String response = String.join("\n",
		        "{",
		        "  'to': [",
		        "    '123456789',",
		        "    '987654321'",
		        "  ],",
		        "  'body': 'Hello, world!',",
		        "  'type' : 'mt_text',",
		        "  'canceled': false,",
		        "  'id': '" + batchId.id() + "',",
		        "  'from': '12345',",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		MtBatchTextSmsResult expectedResponse =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from(sms.from())
		                .to(sms.to())
		                .body(sms.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String path = "/xms/v1/" + username + "/batches";

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(201)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("toktok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			MtBatchTextSmsResult result = conn.sendBatch(sms);
			assertThat(result, is(expectedResponse));
		} finally {
			conn.close();
		}

		wm.verify(postRequestedFor(
		        urlEqualTo(path))
		                .withRequestBody(equalToJson(expectedRequest))
		                .withHeader("Content-Type",
		                        matching("application/json; charset=UTF-8"))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer toktok")));
	}

	@Test
	public void canPostBatchWithSubstitutions() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime =
		        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000,
		                ZoneOffset.UTC);

		MtBatchTextSms sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, ${name}!")
		                .putParameter("name",
		                        ClxApi.buildSubstitution()
		                                .putSubstitution("123456789", "Jane")
		                                .defaultValue("world")
		                                .build())
		                .build();

		String expectedRequest = json.writeValueAsString(sms);

		String response = String.join("\n",
		        "{",
		        "  'to': [",
		        "    '123456789',",
		        "    '987654321'",
		        "  ],",
		        "  'body': 'Hello, ${name}!',",
		        "  'canceled': false,",
		        "  'id': '" + batchId.id() + "',",
		        "  'type' : 'mt_text',",
		        "  'parameters' : {",
		        "    'name' : {",
		        "      '123456789' : 'Jane',",
		        "      'default' : 'world'",
		        "    }",
		        "  },",
		        "  'from': '12345',",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		MtBatchTextSmsResult expectedResponse =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from(sms.from())
		                .to(sms.to())
		                .body(sms.body())
		                .parameters(sms.parameters())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String path = "/xms/v1/" + username + "/batches";

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(201)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			MtBatchTextSmsResult result = conn.sendBatch(sms);
			assertThat(result, is(expectedResponse));
		} finally {
			conn.close();
		}

		wm.verify(postRequestedFor(
		        urlEqualTo(path))
		                .withRequestBody(equalToJson(expectedRequest))
		                .withHeader("Content-Type",
		                        matching("application/json; charset=UTF-8"))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	@Test(expected = ApiException.class)
	public void canHandleBatchPostWithError() throws Throwable {
		String username = TestUtils.freshUsername();

		MtBatchTextSms sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		ImmutableApiError apiError =
		        ImmutableApiError.of("syntax_constraint_violation",
		                "The syntax constraint was violated");
		String response = json.writeValueAsString(apiError);

		String path = "/xms/v1/" + username + "/batches";

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(400)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("toktok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			conn.sendBatch(sms);
			fail("Expected exception, got none");
		} catch (ApiException e) {
			assertThat(e.getCode(), is(apiError.code()));
			assertThat(e.getText(), is(apiError.text()));
			throw e;
		} finally {
			conn.close();
		}
	}

	@Test(expected = JsonParseException.class)
	public void canHandleBatchPostWithInvalidJson() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();

		MtBatchTextSms sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		String response = String.join("\n",
		        "{",
		        "  'to': [",
		        "    '123456789',",
		        "    '987654321'",
		        "  ],",
		        "  'body': 'Hello, world!',",
		        "  'type' 'mt_text',",
		        "  'canceled': false,",
		        "  'id': '" + batchId.id() + "',",
		        "  'from': '12345',",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String path = "/xms/v1/" + username + "/batches";

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(400)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("toktok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			conn.sendBatch(sms);
			fail("Expected exception, got none");
		} finally {
			conn.close();
		}
	}

	@Test
	public void canFetchBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime =
		        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000,
		                ZoneOffset.UTC);

		String path = "/xms/v1/" + username + "/batches/" + batchId.id();
		String response = String.join("\n",
		        "{",
		        "  'to': [",
		        "    '123456789',",
		        "    '987654321'",
		        "  ],",
		        "  'type' : 'mt_text',",
		        "  'body': 'Hello, world!',",
		        "  'canceled': false,",
		        "  'id': '" + batchId.id() + "',",
		        "  'from': '12345',",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		final MtBatchTextSmsResult expected =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			FutureCallback<MtBatchTextSmsResult> testCallback =
			        new FutureCallback<MtBatchTextSmsResult>() {

				        @Override
				        public void failed(Exception ex) {
					        fail("batch unexpectedly failed: "
					                + ex.getMessage());

				        }

				        @Override
				        public void completed(MtBatchTextSmsResult result) {
					        assertThat(result, is(expected));
				        }

				        @Override
				        public void cancelled() {
					        fail("batch unexpectedly cancelled");
				        }
			        };

			MtBatchTextSmsResult result =
			        conn.fetchBatch(batchId, testCallback).get();
			assertThat(result, is(expected));
		} finally {
			conn.close();
		}

		wm.verify(getRequestedFor(
		        urlEqualTo(path))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	@Test
	public void canCancelBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime =
		        OffsetDateTime.of(2016, 10, 2, 9, 34, 28, 542000000,
		                ZoneOffset.UTC);
		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		MtBatchTextSmsResult expected =
		        ImmutableMtBatchTextSmsResult.builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(true)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String response = json.writeValueAsString(expected);

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			MtBatchTextSmsResult result = conn.cancelBatch(batchId).get();
			assertThat(result, is(expected));
		} finally {
			conn.close();
		}

		wm.verify(deleteRequestedFor(
		        urlEqualTo(path))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	@Test
	public void canListBatchesWithEmpty() throws Exception {
		String username = TestUtils.freshUsername();
		String path = "/xms/v1/" + username + "/batches?page=0";
		BatchFilter filter = ImmutableBatchFilter.builder().build();

		final Page<MtBatchSmsResult> expected =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(0)
		                .numPages(0)
		                .build();

		String response = json.writeValueAsString(expected);

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			FutureCallback<Page<MtBatchSmsResult>> testCallback =
			        new FutureCallback<Page<MtBatchSmsResult>>() {

				        @Override
				        public void failed(Exception ex) {
					        fail("batch unexpectedly failed: "
					                + ex.getMessage());
				        }

				        @Override
				        public void completed(Page<MtBatchSmsResult> result) {
					        assertThat(result, is(expected));
				        }

				        @Override
				        public void cancelled() {
					        fail("batch unexpectedly cancelled");
				        }

			        };

			PagedFetcher<MtBatchSmsResult> fetcher =
			        conn.fetchBatches(filter, testCallback);

			Page<MtBatchSmsResult> result =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(result, is(expected));
		} finally {
			conn.close();
		}

		wm.verify(getRequestedFor(
		        urlEqualTo(path))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	@Test
	public void canListBatchesWithTwoPages() throws Exception {
		String username = TestUtils.freshUsername();
		BatchFilter filter = ImmutableBatchFilter.builder().build();

		// Prepare first page.
		String path1 = "/xms/v1/" + username + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        ImmutablePagedBatchResult.builder()
		                .page(0)
		                .size(0)
		                .numPages(2)
		                .build();
		String response1 = json.writeValueAsString(expected1);

		wm.stubFor(get(
		        urlEqualTo(path1))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response1)));

		// Prepare second page.
		String path2 = "/xms/v1/" + username + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        ImmutablePagedBatchResult.builder()
		                .page(1)
		                .size(0)
		                .numPages(2)
		                .build();
		String response2 = json.writeValueAsString(expected2);

		wm.stubFor(get(
		        urlEqualTo(path2))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response2)));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			FutureCallback<Page<MtBatchSmsResult>> testCallback =
			        new FutureCallback<Page<MtBatchSmsResult>>() {

				        @Override
				        public void failed(Exception ex) {
					        fail("batch unexpectedly failed: "
					                + ex.getMessage());
				        }

				        @Override
				        public void completed(Page<MtBatchSmsResult> result) {
					        switch (result.page()) {
					        case 0:
						        assertThat(result, is(expected1));
						        break;
					        case 1:
						        assertThat(result, is(expected2));
						        break;
					        default:
						        fail("unexpected page: " + result);
					        }
				        }

				        @Override
				        public void cancelled() {
					        fail("batch unexpectedly cancelled");
				        }

			        };

			PagedFetcher<MtBatchSmsResult> fetcher =
			        conn.fetchBatches(filter, testCallback);

			Page<MtBatchSmsResult> actual1 =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual1, is(expected1));

			Page<MtBatchSmsResult> actual2 =
			        fetcher.fetchAsync(1, testCallback).get();
			assertThat(actual2, is(expected2));
		} finally {
			conn.close();
		}

		wm.verify(getRequestedFor(
		        urlEqualTo(path1))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));

		wm.verify(getRequestedFor(
		        urlEqualTo(path2))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

}
