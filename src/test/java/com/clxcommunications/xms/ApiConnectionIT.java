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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.api.ApiError;
import com.clxcommunications.xms.api.BatchId;
import com.clxcommunications.xms.api.MtBatchBinarySmsResult;
import com.clxcommunications.xms.api.MtBatchBinarySmsResultImpl;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsResultImpl;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.Page;
import com.clxcommunications.xms.api.PagedBatchResultImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

public class ApiConnectionIT {

	/**
	 * A convenient {@link FutureCallback} for use in tests. By default all
	 * callback methods will call {@link #fail(String)}. Override the one that
	 * should succeed.
	 * 
	 * @param <T>
	 *            the callback result type
	 */
	private static class TestCallback<T> implements FutureCallback<T> {

		@Override
		public void failed(Exception e) {
			fail("API call unexpectedly failed with '" + e.getMessage() + "'");
		}

		@Override
		public void completed(T result) {
			fail("API call unexpectedly completed with '" + result + "'");
		}

		@Override
		public void cancelled() {
			fail("API call unexpectedly cancelled");
		}

	}

	private final ApiObjectMapper json = new ApiObjectMapper();

	@Rule
	public WireMockRule wm = new WireMockRule(
	        WireMockConfiguration.options()
	                .dynamicPort()
	                .dynamicHttpsPort());

	@Rule
	public TestLoggerFactoryResetRule testLoggerFactoryResetRule =
	        new TestLoggerFactoryResetRule();

	@Test
	public void canPostSimpleBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsCreate sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		MtBatchTextSmsResult expectedResponse =
		        MtBatchTextSmsResultImpl.builder()
		                .from(sms.from())
		                .to(sms.to())
		                .body(sms.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String path = "/xms/v1/" + username + "/batches";

		stubPostResponse(expectedResponse, path);

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

		verifyPostRequest(path, sms);
	}

	@Test
	public void canPostBatchWithSubstitutions() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsCreate sms =
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

		MtBatchTextSmsResult expectedResponse =
		        MtBatchTextSmsResultImpl.builder()
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

		stubPostResponse(expectedResponse, path);

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

		verifyPostRequest(path, sms);
	}

	@Test(expected = ApiException.class)
	public void canHandleBatchPostWithError() throws Throwable {
		String username = TestUtils.freshUsername();

		MtBatchTextSmsCreate sms =
		        ClxApi.buildBatchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		ApiError apiError = ApiError.of("syntax_constraint_violation",
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

		MtBatchTextSmsCreate sms =
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
	public void canUpdateSimpleTextBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsUpdate sms =
		        ClxApi.buildBatchTextSmsUpdate()
		                .from("12345")
		                .body("Hello, world!")
		                .unsetDeliveryReport()
		                .unsetExpireAt()
		                .build();

		MtBatchTextSmsResult expectedResponse =
		        MtBatchTextSmsResultImpl.builder()
		                .from(sms.from())
		                .addTo("123")
		                .body(sms.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		stubPostResponse(expectedResponse, path);

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("toktok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			MtBatchTextSmsResult result =
			        conn.updateBatchAsync(batchId, sms, null).get();
			assertThat(result, is(expectedResponse));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, sms);
	}

	@Test
	public void canUpdateSimpleBinaryBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		Set<String> tags = new TreeSet<String>();
		tags.add("tag1");
		tags.add("tag2");

		MtBatchBinarySmsUpdate sms =
		        ClxApi.buildBatchBinarySmsUpdate()
		                .from("12345")
		                .body("howdy".getBytes(TestUtils.US_ASCII))
		                .unsetExpireAt()
		                .build();

		MtBatchBinarySmsResult expectedResponse =
		        MtBatchBinarySmsResultImpl.builder()
		                .from(sms.from())
		                .addTo("123")
		                .body(sms.body())
		                .udh((byte) 1, (byte) 0xff)
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
		                .build();

		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		stubPostResponse(expectedResponse, path);

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("toktok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		try {
			MtBatchBinarySmsResult result =
			        conn.updateBatchAsync(batchId, sms, null).get();
			assertThat(result, is(expectedResponse));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, sms);
	}

	@Test
	public void canFetchBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		final MtBatchTextSmsResult expected =
		        MtBatchTextSmsResultImpl.builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId)
		                .createdAt(smsTime)
		                .modifiedAt(smsTime)
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
			FutureCallback<MtBatchTextSmsResult> testCallback =
			        new TestCallback<MtBatchTextSmsResult>() {

				        @Override
				        public void completed(MtBatchTextSmsResult result) {
					        assertThat(result, is(expected));
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
	public void canHandle404WhenFetchingBatch() throws Throwable {
		String username = TestUtils.freshUsername();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(404)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())
		                        .withBody("BAD")));

		ApiConnection conn = ApiConnection.builder()
		        .username(username)
		        .token("tok")
		        .endpointHost("localhost", wm.port(), "http")
		        .start();

		/*
		 * The exception we'll receive in the callback. Need to store it to
		 * verify that it is the same exception as received from #get().
		 */
		final AtomicReference<Exception> failException =
		        new AtomicReference<Exception>();

		try {
			/*
			 * Used to make sure callback and test thread are agreeing about the
			 * failException variable.
			 */
			final CyclicBarrier barrier = new CyclicBarrier(2);

			FutureCallback<MtBatchTextSmsResult> testCallback =
			        new TestCallback<MtBatchTextSmsResult>() {

				        @Override
				        public void failed(Exception exception) {
					        if (!failException.compareAndSet(null,
					                exception)) {
						        fail("failed called multiple times");
					        }

					        try {
						        barrier.await();
					        } catch (Exception e) {
						        throw new RuntimeException(e);
					        }
				        }

			        };

			Future<MtBatchTextSmsResult> future =
			        conn.fetchBatch(batchId, testCallback);

			// Give plenty of time for the callback to be called.
			barrier.await(1, TimeUnit.SECONDS);

			future.get();
			fail("unexpected future get success");
		} catch (ExecutionException executionException) {
			/*
			 * The exception cause should be the same as we received in the
			 * callback.
			 */
			assertThat(failException.get(),
			        is(theInstance(executionException.getCause())));

			assertThat(executionException.getCause(),
			        is(instanceOf(UnexpectedResponseException.class)));

			UnexpectedResponseException ure =
			        (UnexpectedResponseException) executionException.getCause();

			assertThat(ure.getResponse(), notNullValue());

			assertThat(ure.getResponse().getStatusLine()
			        .getStatusCode(), is(404));

			assertThat(
			        ure.getResponse().getEntity().getContentType().getValue(),
			        is(ContentType.TEXT_PLAIN.toString()));

			byte[] buf = new byte[100];
			int read;

			InputStream contentStream = null;
			try {
				contentStream = ure.getResponse().getEntity().getContent();
				read = contentStream.read(buf);
			} catch (IOException ioe) {
				throw new AssertionError(
				        "unexpected exception: "
				                + ioe.getMessage(),
				        ioe);
			} finally {
				if (contentStream != null) {
					try {
						contentStream.close();
					} catch (IOException ioe) {
						throw new AssertionError(
						        "unexpected exception: " + ioe.getMessage(),
						        ioe);
					}
				}
			}

			assertThat(read, is(3));
			assertThat(Arrays.copyOf(buf, 3),
			        is(new byte[] { 'B', 'A', 'D' }));
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
		OffsetDateTime smsTime = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		String path = "/xms/v1/" + username + "/batches/" + batchId.id();

		MtBatchTextSmsResult expected =
		        MtBatchTextSmsResultImpl.builder()
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
		BatchFilter filter = BatchFilterImpl.builder().build();

		final Page<MtBatchSmsResult> expected =
		        PagedBatchResultImpl.builder()
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
			        new TestCallback<Page<MtBatchSmsResult>>() {

				        @Override
				        public void completed(Page<MtBatchSmsResult> result) {
					        assertThat(result, is(expected));
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
		BatchFilter filter = BatchFilterImpl.builder().build();

		// Prepare first page.
		String path1 = "/xms/v1/" + username + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        PagedBatchResultImpl.builder()
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
		        PagedBatchResultImpl.builder()
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
			        new TestCallback<Page<MtBatchSmsResult>>() {

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

	/**
	 * Helper that sets up WireMock to respond using a JSON body.
	 * 
	 * @param response
	 *            the response to give, serialized to JSON
	 * @param path
	 *            the path on which to listen
	 * @throws JsonProcessingException
	 *             if the given response object could not be serialized
	 */
	private void stubPostResponse(Object response, String path)
	        throws JsonProcessingException {
		byte[] body = json.writeValueAsBytes(response);

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(201)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(body)));
	}

	/**
	 * Helper that sets up WireMock to verify that a request matches a given
	 * object in JSON format.
	 * 
	 * @param path
	 *            the request path to match
	 * @param request
	 *            the request object whose JSON serialization should match
	 * @throws JsonProcessingException
	 *             if the given request object could not be serialized
	 */
	private void verifyPostRequest(String path, Object request)
	        throws JsonProcessingException {
		String expectedRequest = json.writeValueAsString(request);

		wm.verify(postRequestedFor(
		        urlEqualTo(path))
		                .withRequestBody(equalToJson(expectedRequest))
		                .withHeader("Content-Type",
		                        matching("application/json; charset=UTF-8"))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer toktok")));
	}

}
