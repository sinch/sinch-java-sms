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
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.clxcommunications.testsupport.TestUtils;
import com.clxcommunications.xms.api.ApiError;
import com.clxcommunications.xms.api.AutoUpdate;
import com.clxcommunications.xms.api.BatchDeliveryReport;
import com.clxcommunications.xms.api.BatchId;
import com.clxcommunications.xms.api.DeliveryStatus;
import com.clxcommunications.xms.api.GroupCreate;
import com.clxcommunications.xms.api.GroupId;
import com.clxcommunications.xms.api.GroupResponse;
import com.clxcommunications.xms.api.GroupUpdate;
import com.clxcommunications.xms.api.MoBinarySms;
import com.clxcommunications.xms.api.MoSms;
import com.clxcommunications.xms.api.MoTextSms;
import com.clxcommunications.xms.api.MtBatchBinarySmsCreate;
import com.clxcommunications.xms.api.MtBatchBinarySmsResult;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchDryRunResult;
import com.clxcommunications.xms.api.MtBatchSmsCreate;
import com.clxcommunications.xms.api.MtBatchSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.Page;
import com.clxcommunications.xms.api.PagedBatchResult;
import com.clxcommunications.xms.api.PagedGroupResult;
import com.clxcommunications.xms.api.PagedInboundsResult;
import com.clxcommunications.xms.api.RecipientDeliveryReport;
import com.clxcommunications.xms.api.Tags;
import com.clxcommunications.xms.api.TagsUpdate;
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
	public void canCreateBinaryBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		MtBatchBinarySmsCreate request =
		        ClxApi.batchBinarySms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("body".getBytes(TestUtils.US_ASCII))
		                .udh("udh".getBytes(TestUtils.US_ASCII))
		                .build();

		MtBatchBinarySmsResult expected =
		        new MtBatchBinarySmsResult.Builder()
		                .from(request.from())
		                .to(request.to())
		                .body(request.body())
		                .udh(request.udh())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches";

		stubPostResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchBinarySmsResult actual = conn.createBatch(request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canCreateTextBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsCreate request =
		        ClxApi.batchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world! Здравей свят!")
		                .build();

		MtBatchTextSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from(request.from())
		                .to(request.to())
		                .body(request.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches";

		stubPostResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchTextSmsResult actual = conn.createBatch(request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canCreateTextBatchWithSubstitutions() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsCreate request =
		        ClxApi.batchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, ${name}!")
		                .putParameter("name",
		                        ClxApi.parameterValues()
		                                .putSubstitution("123456789", "Jane")
		                                .defaultValue("world")
		                                .build())
		                .build();

		MtBatchTextSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from(request.from())
		                .to(request.to())
		                .body(request.body())
		                .parameters(request.parameters())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches";

		stubPostResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchTextSmsResult actual = conn.createBatch(request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canHandleBatchCreateWithError() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		MtBatchTextSmsCreate request =
		        ClxApi.batchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		ApiError apiError = ApiError.of("syntax_constraint_violation",
		        "The syntax constraint was violated");

		String path = "/v1/" + spid + "/batches";

		stubPostResponse(apiError, path, 400);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.createBatch(request);
			fail("Expected exception, got none");
		} catch (ErrorResponseException e) {
			assertThat(e.getCode(), is(apiError.code()));
			assertThat(e.getText(), is(apiError.text()));
		} finally {
			conn.close();
		}
	}

	@Test
	public void canHandleBatchCreateWithInvalidJson() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		MtBatchTextSmsCreate request =
		        ClxApi.batchTextSms()
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
		        "  'id': '" + batchId + "',",
		        "  'from': '12345',",
		        "  'created_at': '2016-10-02T09:34:28.542Z',",
		        "  'modified_at': '2016-10-02T09:34:28.542Z'",
		        "}").replace('\'', '"');

		String path = "/v1/" + spid + "/batches";

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(201)
		                .withHeader("Content-Type",
		                        "application/json; charset=UTF-8")
		                .withBody(response)));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.createBatch(request);
			fail("Expected exception, got none");
		} catch (ConcurrentException e) {
			assertThat(e.getCause(), is(instanceOf(JsonParseException.class)));
		} finally {
			conn.close();
		}
	}

	@Test
	public void canReplaceBinaryBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		MtBatchBinarySmsCreate request =
		        ClxApi.batchBinarySms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("body".getBytes(TestUtils.US_ASCII))
		                .udh("udh".getBytes(TestUtils.US_ASCII))
		                .build();

		MtBatchBinarySmsResult expected =
		        new MtBatchBinarySmsResult.Builder()
		                .from(request.from())
		                .to(request.to())
		                .body(request.body())
		                .udh(request.udh())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches/" + batchId;

		stubPutResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchBinarySmsResult actual = conn.replaceBatch(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canReplaceTextBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsCreate request =
		        ClxApi.batchTextSms()
		                .from("12345")
		                .addTo("123456789")
		                .addTo("987654321")
		                .body("Hello, world!")
		                .build();

		MtBatchTextSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from(request.from())
		                .to(request.to())
		                .body(request.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches/" + batchId;

		stubPutResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchTextSmsResult actual = conn.replaceBatch(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canUpdateSimpleTextBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		MtBatchTextSmsUpdate request =
		        ClxApi.batchTextSmsUpdate()
		                .from("12345")
		                .body("Hello, world!")
		                .unsetDeliveryReport()
		                .unsetExpireAt()
		                .build();

		MtBatchTextSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from(request.from())
		                .addTo("123")
		                .body(request.body())
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches/" + batchId;

		stubPostResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchTextSmsResult actual = conn.updateBatch(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canUpdateSimpleBinaryBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		Set<String> tags = new TreeSet<String>();
		tags.add("tag1");
		tags.add("tag2");

		MtBatchBinarySmsUpdate request =
		        ClxApi.batchBinarySmsUpdate()
		                .from("12345")
		                .body("howdy".getBytes(TestUtils.US_ASCII))
		                .unsetExpireAt()
		                .build();

		MtBatchBinarySmsResult expected =
		        new MtBatchBinarySmsResult.Builder()
		                .from(request.from())
		                .addTo("123")
		                .body(request.body())
		                .udh((byte) 1, (byte) 0xff)
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		String path = "/v1/" + spid + "/batches/" + batchId;

		stubPostResponse(expected, path, 201);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchBinarySmsResult actual = conn.updateBatch(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canFetchTextBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		String path = "/v1/" + spid + "/batches/" + batchId;

		final MtBatchSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchSmsResult actual = conn.fetchBatch(batchId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchTextBatchAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		String path = "/v1/" + spid + "/batches/" + batchId;

		final MtBatchSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<MtBatchSmsResult> testCallback =
			        new TestCallback<MtBatchSmsResult>() {

				        @Override
				        public void completed(MtBatchSmsResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			MtBatchSmsResult actual =
			        conn.fetchBatchAsync(batchId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchBinaryBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);

		String path = "/v1/" + spid + "/batches/" + batchId;

		final MtBatchSmsResult expected =
		        new MtBatchBinarySmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body((byte) 0xf0, (byte) 0x0f)
		                .udh((byte) 0x50, (byte) 0x05)
		                .canceled(false)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<MtBatchSmsResult> testCallback =
			        new TestCallback<MtBatchSmsResult>() {

				        @Override
				        public void completed(MtBatchSmsResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			MtBatchSmsResult actual =
			        conn.fetchBatchAsync(batchId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canHandle404WhenFetchingBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId;

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(404)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())
		                        .withBody("BAD")));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
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
			final CountDownLatch latch = new CountDownLatch(1);

			FutureCallback<MtBatchSmsResult> testCallback =
			        new TestCallback<MtBatchSmsResult>() {

				        @Override
				        public void failed(Exception exception) {
					        if (!failException.compareAndSet(null,
					                exception)) {
						        fail("failed called multiple times");
					        }

					        latch.countDown();
				        }

			        };

			Future<MtBatchSmsResult> future =
			        conn.fetchBatchAsync(batchId, testCallback);

			// Give plenty of time for the callback to be called.
			latch.await();

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

		verifyGetRequest(path);
	}

	@Test
	public void canCancelBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.of(2016, 10, 2, 9, 34, 28,
		        542000000, ZoneOffset.UTC);
		String path = "/v1/" + spid + "/batches/" + batchId;

		MtBatchSmsResult expected =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(true)
		                .id(batchId)
		                .createdAt(time)
		                .modifiedAt(time)
		                .build();

		stubDeleteResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchSmsResult result = conn.cancelBatch(batchId);
			assertThat(result, is(expected));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	/**
	 * Verifies that the default HTTP client actually can handle multiple
	 * simultaneous requests.
	 * 
	 * @throws Exception
	 *             shouldn't happen
	 */
	@Test
	public void canCancelBatchConcurrently() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		// Set up the first request (the one that will be delayed).
		MtBatchSmsResult expected1 =
		        new MtBatchTextSmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!")
		                .canceled(true)
		                .id(TestUtils.freshBatchId())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		String path1 = "/v1/" + spid + "/batches/" + expected1.id();
		byte[] response1 = json.writeValueAsBytes(expected1);

		wm.stubFor(delete(
		        urlEqualTo(path1))
		                .willReturn(aResponse()
		                        .withFixedDelay(500) // Delay for a while.
		                        .withStatus(200)
		                        .withHeader("Content-Type",
		                                "application/json; charset=UTF-8")
		                        .withBody(response1)));

		// Set up the second request.
		MtBatchSmsResult expected2 =
		        new MtBatchBinarySmsResult.Builder()
		                .from("12345")
		                .addTo("123456789", "987654321")
		                .body("Hello, world!".getBytes())
		                .udh((byte) 1)
		                .canceled(true)
		                .id(TestUtils.freshBatchId())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		String path2 = "/v1/" + spid + "/batches/" + expected2.id();

		stubDeleteResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			final Queue<MtBatchSmsResult> results =
			        new ConcurrentArrayQueue<MtBatchSmsResult>();
			final CountDownLatch latch = new CountDownLatch(2);

			FutureCallback<MtBatchSmsResult> callback =
			        new TestCallback<MtBatchSmsResult>() {

				        @Override
				        public void completed(MtBatchSmsResult result) {
					        results.add(result);
					        latch.countDown();
				        }

			        };

			conn.cancelBatchAsync(expected1.id(), callback);
			Thread.sleep(100);
			conn.cancelBatchAsync(expected2.id(), callback);

			// Wait for callback to be called.
			latch.await();

			// We expect the second message to be handled first.
			assertThat(results.size(), is(2));
			assertThat(results.poll(), is(expected2));
			assertThat(results.poll(), is(expected1));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path1);
		verifyDeleteRequest(path2);
	}

	@Test
	public void canListBatchesWithEmpty() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		String path = "/v1/" + spid + "/batches?page=0";
		BatchFilter filter = ClxApi.batchFilter().build();

		final Page<MtBatchSmsResult> expected =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(0)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Page<MtBatchSmsResult>> testCallback =
			        new TestCallback<Page<MtBatchSmsResult>>() {

				        @Override
				        public void completed(Page<MtBatchSmsResult> result) {
					        assertThat(result, is(expected));
				        }

			        };

			PagedFetcher<MtBatchSmsResult> fetcher = conn.fetchBatches(filter);

			Page<MtBatchSmsResult> actual =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canListBatchesWithTwoPages() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchFilter filter = ClxApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        new PagedBatchResult.Builder()
		                .page(1)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
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

			PagedFetcher<MtBatchSmsResult> fetcher = conn.fetchBatches(filter);

			Page<MtBatchSmsResult> actual1 =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual1, is(expected1));

			Page<MtBatchSmsResult> actual2 =
			        fetcher.fetchAsync(1, testCallback).get();
			assertThat(actual2, is(expected2));
		} finally {
			conn.close();
		}

		verifyGetRequest(path1);
		verifyGetRequest(path2);
	}

	@Test
	public void canIterateOverPages() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchFilter filter = ClxApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(1)
		                .totalSize(2)
		                .addContent(
		                        new MtBatchTextSmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        new PagedBatchResult.Builder()
		                .page(1)
		                .size(2)
		                .totalSize(2)
		                .addContent(
		                        new MtBatchBinarySmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body((byte) 0)
		                                .udh((byte) 1)
		                                .canceled(false)
		                                .build())
		                .addContent(
		                        new MtBatchTextSmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			PagedFetcher<MtBatchSmsResult> fetcher = conn.fetchBatches(filter);

			List<Page<MtBatchSmsResult>> actuals =
			        new ArrayList<Page<MtBatchSmsResult>>();

			for (Page<MtBatchSmsResult> result : fetcher.pages()) {
				actuals.add(result);
			}

			List<Page<MtBatchSmsResult>> expecteds =
			        new ArrayList<Page<MtBatchSmsResult>>();
			expecteds.add(expected1);
			expecteds.add(expected2);

			assertThat(actuals, is(expecteds));
		} finally {
			conn.close();
		}

		verifyGetRequest(path1);
		verifyGetRequest(path2);
	}

	@Test
	public void canIterateOverBatchesWithTwoPages() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchFilter filter = ClxApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        new PagedBatchResult.Builder()
		                .page(0)
		                .size(1)
		                .totalSize(3)
		                .addContent(
		                        new MtBatchTextSmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        new PagedBatchResult.Builder()
		                .page(1)
		                .size(2)
		                .totalSize(3)
		                .addContent(
		                        new MtBatchBinarySmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body((byte) 0)
		                                .udh((byte) 1)
		                                .canceled(false)
		                                .build())
		                .addContent(
		                        new MtBatchTextSmsResult.Builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			PagedFetcher<MtBatchSmsResult> fetcher = conn.fetchBatches(filter);

			List<MtBatchSmsResult> actuals =
			        new ArrayList<MtBatchSmsResult>();

			for (MtBatchSmsResult result : fetcher.elements()) {
				actuals.add(result);
			}

			List<MtBatchSmsResult> expecteds =
			        new ArrayList<MtBatchSmsResult>();
			expecteds.addAll(expected1.content());
			expecteds.addAll(expected2.content());

			assertThat(actuals, is(expecteds));
		} finally {
			conn.close();
		}

		verifyGetRequest(path1);
		verifyGetRequest(path2);
	}

	@Test
	public void canFetchDeliveryReportSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId
		        + "/delivery_report"
		        + "?type=full&status=Aborted%2CDelivered&code=200%2C300";

		final BatchDeliveryReport expected =
		        new BatchDeliveryReport.Builder()
		                .batchId(batchId)
		                .totalMessageCount(1010)
		                .addStatus(
		                        new BatchDeliveryReport.Status.Builder()
		                                .code(200)
		                                .status(DeliveryStatus.ABORTED)
		                                .count(10)
		                                .addRecipient("rec1", "rec2")
		                                .build())
		                .addStatus(
		                        new BatchDeliveryReport.Status.Builder()
		                                .code(300)
		                                .status(DeliveryStatus.DELIVERED)
		                                .count(20)
		                                .addRecipient("rec3", "rec4", "rec5")
		                                .build())
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		BatchDeliveryReportParams filter =
		        ClxApi.batchDeliveryReportParams()
		                .fullReport()
		                .addStatus(DeliveryStatus.ABORTED,
		                        DeliveryStatus.DELIVERED)
		                .addCode(200, 300)
		                .build();

		try {
			BatchDeliveryReport actual =
			        conn.fetchDeliveryReport(batchId, filter);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchDeliveryReportAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId
		        + "/delivery_report?type=full";

		final BatchDeliveryReport expected =
		        new BatchDeliveryReport.Builder()
		                .batchId(batchId)
		                .totalMessageCount(1010)
		                .addStatus(
		                        new BatchDeliveryReport.Status.Builder()
		                                .code(200)
		                                .status(DeliveryStatus.ABORTED)
		                                .count(10)
		                                .addRecipient("rec1", "rec2")
		                                .build())
		                .addStatus(
		                        new BatchDeliveryReport.Status.Builder()
		                                .code(300)
		                                .status(DeliveryStatus.DELIVERED)
		                                .count(20)
		                                .addRecipient("rec3", "rec4", "rec5")
		                                .build())
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		BatchDeliveryReportParams filter =
		        ClxApi.batchDeliveryReportParams()
		                .fullReport()
		                .build();

		try {
			FutureCallback<BatchDeliveryReport> testCallback =
			        new TestCallback<BatchDeliveryReport>() {

				        @Override
				        public void completed(BatchDeliveryReport result) {
					        assertThat(result, is(expected));
				        }

			        };

			BatchDeliveryReport actual = conn.fetchDeliveryReportAsync(
			        batchId, filter, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchRecipientDeliveryReportSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		String recipient = "987654321";

		String path = "/v1/" + spid + "/batches/" + batchId
		        + "/delivery_report/" + recipient;

		final RecipientDeliveryReport expected =
		        new RecipientDeliveryReport.Builder()
		                .batchId(batchId)
		                .recipient(recipient)
		                .code(200)
		                .status(DeliveryStatus.ABORTED)
		                .statusMessage("this is the status")
		                .operator("10101")
		                .at(OffsetDateTime.now())
		                .operatorStatusAt(OffsetDateTime.now(Clock.systemUTC()))
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			RecipientDeliveryReport actual =
			        conn.fetchDeliveryReport(batchId, recipient);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchRecipientDeliveryReportAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		String recipient = "987654321";

		String path = "/v1/" + spid + "/batches/" + batchId
		        + "/delivery_report/" + recipient;

		final RecipientDeliveryReport expected =
		        new RecipientDeliveryReport.Builder()
		                .batchId(batchId)
		                .recipient(recipient)
		                .code(200)
		                .status(DeliveryStatus.ABORTED)
		                .statusMessage("this is the status")
		                .operator("10101")
		                .at(OffsetDateTime.now())
		                .operatorStatusAt(OffsetDateTime.now(Clock.systemUTC()))
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<RecipientDeliveryReport> testCallback =
			        new TestCallback<RecipientDeliveryReport>() {

				        @Override
				        public void completed(RecipientDeliveryReport result) {
					        assertThat(result, is(expected));
				        }

			        };

			RecipientDeliveryReport actual = conn.fetchDeliveryReportAsync(
			        batchId, recipient, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canUpdateBatchTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		TagsUpdate request =
		        new TagsUpdate.Builder()
		                .addTagInsertion("aTag1", "аТаг2")
		                .addTagRemoval("rTag1", "rТаг2")
		                .build();

		Tags expected = Tags.of("tag1", "таг2");

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.updateTags(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canUpdateBatchTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		TagsUpdate request =
		        new TagsUpdate.Builder()
		                .addTagInsertion("aTag1", "аТаг2")
		                .addTagRemoval("rTag1", "rТаг2")
		                .build();

		final Tags expected = Tags.of("tag1", "таг2");

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual =
			        conn.updateTagsAsync(batchId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canReplaceBatchTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		Tags request = Tags.of("rTag1", "rTag2");

		Tags expected = Tags.of("tag1", "таг2");

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.replaceTags(batchId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canReplaceBatchTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		Tags request = Tags.of("rTag1", "rTag2");

		final Tags expected = Tags.of("tag1", "таг2");

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual =
			        conn.replaceTagsAsync(batchId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canFetchBatchTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		Tags expected = Tags.of("tag1", "таг2");

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.fetchTags(batchId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchBatchTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		final Tags expected = Tags.of("tag1", "таг2");

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual = conn.fetchTagsAsync(batchId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canCreateBatchDryRunSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		String path = "/v1/" + spid + "/batches/dry_run";

		MtBatchSmsCreate request = ClxApi.batchTextSms()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!")
		        .build();

		final MtBatchDryRunResult expected =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MtBatchDryRunResult actual =
			        conn.createBatchDryRun(request, null, null);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canCreateBatchDryRunAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		String path = "/v1/" + spid + "/batches/dry_run"
		        + "?per_recipient=true&number_of_recipients=5";

		MtBatchSmsCreate request = ClxApi.batchTextSms()
		        .from("1234")
		        .addTo("987654321")
		        .body("Hello, world!")
		        .build();

		final MtBatchDryRunResult expected =
		        new MtBatchDryRunResult.Builder()
		                .numberOfRecipients(20)
		                .numberOfMessages(200)
		                .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<MtBatchDryRunResult> testCallback =
			        new TestCallback<MtBatchDryRunResult>() {

				        @Override
				        public void completed(MtBatchDryRunResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			MtBatchDryRunResult actual =
			        conn.createBatchDryRunAsync(request, true, 5, testCallback)
			                .get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canCreateGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups";

		GroupCreate request =
		        ClxApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			GroupResponse actual = conn.createGroup(request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canCreateGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups";

		GroupCreate request =
		        ClxApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		final GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<GroupResponse> testCallback =
			        new TestCallback<GroupResponse>() {

				        @Override
				        public void completed(GroupResponse result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResponse actual =
			        conn.createGroupAsync(request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canFetchGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			GroupResponse actual = conn.fetchGroup(groupId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		final GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<GroupResponse> testCallback =
			        new TestCallback<GroupResponse>() {

				        @Override
				        public void completed(GroupResponse result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResponse actual =
			        conn.fetchGroupAsync(groupId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchGroupMembersSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/members";

		Set<String> expected = new HashSet<String>(
		        Arrays.asList("mem1", "mem2", "mem3"));

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Set<String> actual = conn.fetchGroupMembers(groupId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchGroupMembersAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/members";

		final Set<String> expected = new HashSet<String>(
		        Arrays.asList("mem1", "mem2", "mem3"));

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Set<String>> testCallback =
			        new TestCallback<Set<String>>() {

				        @Override
				        public void completed(Set<String> result) {
					        assertThat(result, is(expected));
				        }

			        };

			Set<String> actual =
			        conn.fetchGroupMembersAsync(groupId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canListGroupsWithEmpty() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		String path = "/v1/" + spid + "/groups?page=0";
		GroupFilter filter = ClxApi.groupFilter().build();

		final Page<GroupResponse> expected =
		        new PagedGroupResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(0)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Page<GroupResponse>> testCallback =
			        new TestCallback<Page<GroupResponse>>() {

				        @Override
				        public void completed(Page<GroupResponse> result) {
					        assertThat(result, is(expected));
				        }

			        };

			PagedFetcher<GroupResponse> fetcher = conn.fetchGroups(filter);

			Page<GroupResponse> actual =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canListGroupsWithTwoPages() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupFilter filter = ClxApi.groupFilter()
		        .addTag("tag1", "таг2")
		        .build();

		// Prepare first page.
		String path1 = "/v1/" + spid
		        + "/groups?page=0&tags=tag1%2C%D1%82%D0%B0%D0%B32";

		final Page<GroupResponse> expected1 =
		        new PagedGroupResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid
		        + "/groups?page=1&tags=tag1%2C%D1%82%D0%B0%D0%B32";

		final Page<GroupResponse> expected2 =
		        new PagedGroupResult.Builder()
		                .page(1)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Page<GroupResponse>> testCallback =
			        new TestCallback<Page<GroupResponse>>() {

				        @Override
				        public void completed(Page<GroupResponse> result) {
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

			PagedFetcher<GroupResponse> fetcher = conn.fetchGroups(filter);

			Page<GroupResponse> actual1 =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual1, is(expected1));

			Page<GroupResponse> actual2 =
			        fetcher.fetchAsync(1, testCallback).get();
			assertThat(actual2, is(expected2));
		} finally {
			conn.close();
		}

		verifyGetRequest(path1);
		verifyGetRequest(path2);
	}

	@Test
	public void canUpdateGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupUpdate request = GroupUpdate.builder()
		        .unsetName()
		        .addMemberAdd("123456789")
		        .addMemberRemove("987654321")
		        .build();

		GroupResponse expected = new GroupResponse.Builder()
		        .size(72)
		        .id(groupId)
		        .createdAt(OffsetDateTime.now(Clock.systemUTC()))
		        .modifiedAt(OffsetDateTime.now(Clock.systemUTC()))
		        .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			GroupResponse actual = conn.updateGroup(groupId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canUpdateGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupUpdate request = GroupUpdate.builder()
		        .unsetName()
		        .addMemberAdd("123456789")
		        .addMemberRemove("987654321")
		        .build();

		final GroupResponse expected = new GroupResponse.Builder()
		        .size(72)
		        .id(groupId)
		        .createdAt(OffsetDateTime.now(Clock.systemUTC()))
		        .modifiedAt(OffsetDateTime.now(Clock.systemUTC()))
		        .build();

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<GroupResponse> testCallback =
			        new TestCallback<GroupResponse>() {

				        @Override
				        public void completed(GroupResponse result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResponse actual =
			        conn.updateGroupAsync(groupId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canReplaceGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupCreate request =
		        ClxApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			GroupResponse actual = conn.replaceGroup(groupId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canReplaceGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupCreate request =
		        ClxApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		final GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<GroupResponse> testCallback =
			        new TestCallback<GroupResponse>() {

				        @Override
				        public void completed(GroupResponse result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResponse actual = conn
			        .replaceGroupAsync(groupId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canDeleteGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubDeleteResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.deleteGroup(groupId);
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canDeleteGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		final GroupResponse expected =
		        new GroupResponse.Builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(AutoUpdate.builder()
		                        .to("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .createdAt(OffsetDateTime.now())
		                .modifiedAt(OffsetDateTime.now())
		                .build();

		stubDeleteResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Void> testCallback =
			        new TestCallback<Void>() {

				        @Override
				        public void completed(Void result) {
					        assertThat(result, is(nullValue()));
				        }

			        };

			conn.deleteGroupAsync(groupId, testCallback).get();
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canUpdateGroupTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		TagsUpdate request =
		        new TagsUpdate.Builder()
		                .addTagInsertion("aTag1", "аТаг2")
		                .addTagRemoval("rTag1", "rТаг2")
		                .build();

		Tags expected = Tags.of("tag1", "таг2");

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.updateTags(groupId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canUpdateGroupTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		TagsUpdate request =
		        new TagsUpdate.Builder()
		                .addTagInsertion("aTag1", "аТаг2")
		                .addTagRemoval("rTag1", "rТаг2")
		                .build();

		final Tags expected = Tags.of("tag1", "таг2");

		stubPostResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual =
			        conn.updateTagsAsync(groupId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPostRequest(path, request);
	}

	@Test
	public void canReplaceGroupTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		Tags request = Tags.of("rTag1", "rTag2");

		Tags expected = Tags.of("tag1", "таг2");

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.replaceTags(groupId, request);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canReplaceGroupTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		Tags request = Tags.of("rTag1", "rTag2");

		final Tags expected = Tags.of("tag1", "таг2");

		stubPutResponse(expected, path, 200);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual =
			        conn.replaceTagsAsync(groupId, request, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyPutRequest(path, request);
	}

	@Test
	public void canFetchGroupTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		Tags expected = Tags.of("tag1", "таг2");

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			Tags actual = conn.fetchTags(groupId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchGroupTagsAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		final Tags expected = Tags.of("tag1", "таг2");

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Tags> testCallback =
			        new TestCallback<Tags>() {

				        @Override
				        public void completed(Tags result) {
					        assertThat(result, is(expected));
				        }

			        };

			Tags actual = conn.fetchTagsAsync(groupId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canListInboundsWithEmpty() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		String path = "/v1/" + spid + "/inbounds?page=0";
		InboundsFilter filter = ClxApi.inboundsFilter().build();

		final Page<MoSms> expected =
		        new PagedInboundsResult.Builder()
		                .page(0)
		                .size(0)
		                .totalSize(0)
		                .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Page<MoSms>> testCallback =
			        new TestCallback<Page<MoSms>>() {

				        @Override
				        public void completed(Page<MoSms> result) {
					        assertThat(result, is(expected));
				        }

			        };

			PagedFetcher<MoSms> fetcher = conn.fetchInbounds(filter);

			Page<MoSms> actual =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canListInboundsWithTwoPages() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		InboundsFilter filter = ClxApi.inboundsFilter()
		        .addTo("10101")
		        .build();
		String inboundsId1 = TestUtils.freshSmsId();
		String inboundsId2 = TestUtils.freshSmsId();
		OffsetDateTime time1 = OffsetDateTime.now(Clock.systemUTC());
		OffsetDateTime time2 = OffsetDateTime.now(Clock.systemUTC());

		// Prepare first page.
		String path1 = "/v1/" + spid + "/inbounds?page=0&to=10101";

		final Page<MoSms> expected1 =
		        new PagedInboundsResult.Builder()
		                .page(0)
		                .size(1)
		                .totalSize(2)
		                .addContent(new MoTextSms.Builder()
		                        .from("987654321")
		                        .to("54321")
		                        .id(inboundsId1)
		                        .receivedAt(time1)
		                        .sentAt(time2)
		                        .body("body1")
		                        .build())
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/inbounds?page=1&to=10101";

		final Page<MoSms> expected2 =
		        new PagedInboundsResult.Builder()
		                .page(1)
		                .size(1)
		                .totalSize(2)
		                .addContent(new MoBinarySms.Builder()
		                        .from("123456789")
		                        .to("12345")
		                        .id(inboundsId2)
		                        .receivedAt(time2)
		                        .sentAt(time1)
		                        .body("body2".getBytes(TestUtils.US_ASCII))
		                        .udh("udh".getBytes(TestUtils.US_ASCII))
		                        .build())
		                .build();

		stubGetResponse(expected2, path2);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<Page<MoSms>> testCallback =
			        new TestCallback<Page<MoSms>>() {

				        @Override
				        public void completed(Page<MoSms> result) {
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

			PagedFetcher<MoSms> fetcher = conn.fetchInbounds(filter);

			Page<MoSms> actual1 = fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual1, is(expected1));

			Page<MoSms> actual2 = fetcher.fetchAsync(1, testCallback).get();
			assertThat(actual2, is(expected2));
		} finally {
			conn.close();
		}

		verifyGetRequest(path1);
		verifyGetRequest(path2);
	}

	@Test
	public void canFetchInboundSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		String smsId = TestUtils.freshSmsId();
		String inboundsId = TestUtils.freshSmsId();
		OffsetDateTime time1 = OffsetDateTime.now(Clock.systemUTC());
		OffsetDateTime time2 = OffsetDateTime.now(Clock.systemUTC());

		String path = "/v1/" + spid + "/inbounds/" + smsId;

		MoSms expected = new MoBinarySms.Builder()
		        .from("123456789")
		        .to("12345")
		        .id(inboundsId)
		        .receivedAt(time1)
		        .sentAt(time2)
		        .body("body2".getBytes(TestUtils.US_ASCII))
		        .udh("udh".getBytes(TestUtils.US_ASCII))
		        .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			MoSms actual = conn.fetchInbound(smsId);
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	@Test
	public void canFetchInboundAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		String smsId = TestUtils.freshSmsId();
		String inboundsId = TestUtils.freshSmsId();
		OffsetDateTime time1 = OffsetDateTime.now(Clock.systemUTC());
		OffsetDateTime time2 = OffsetDateTime.now(Clock.systemUTC());

		String path = "/v1/" + spid + "/inbounds/" + smsId;

		final MoSms expected = new MoBinarySms.Builder()
		        .from("123456789")
		        .to("12345")
		        .id(inboundsId)
		        .receivedAt(time1)
		        .sentAt(time2)
		        .body("body2".getBytes(TestUtils.US_ASCII))
		        .udh("udh".getBytes(TestUtils.US_ASCII))
		        .build();

		stubGetResponse(expected, path);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			FutureCallback<MoSms> testCallback =
			        new TestCallback<MoSms>() {

				        @Override
				        public void completed(MoSms result) {
					        assertThat(result, is(expected));
				        }

			        };

			MoSms actual = conn.fetchInboundAsync(smsId, testCallback).get();
			assertThat(actual, is(expected));
		} finally {
			conn.close();
		}

		verifyGetRequest(path);
	}

	/**
	 * Helper that sets up WireMock to respond to a GET using a JSON body.
	 * 
	 * @param response
	 *            the response to give, serialized to JSON
	 * @param path
	 *            the path on which to listen
	 * @param status
	 *            the response HTTP status
	 * @throws JsonProcessingException
	 *             if the given response object could not be serialized
	 */
	private void stubGetResponse(Object response, String path)
	        throws JsonProcessingException {
		byte[] body = json.writeValueAsBytes(response);

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type", "application/json")
		                        .withBody(body)));
	}

	/**
	 * Helper that sets up WireMock to verify a GET request.
	 * 
	 * @param path
	 *            the request path to match
	 */
	private void verifyGetRequest(String path) {
		wm.verify(getRequestedFor(
		        urlEqualTo(path))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	/**
	 * Helper that sets up WireMock to respond to a DELETE using a JSON body.
	 * 
	 * @param response
	 *            the response to give, serialized to JSON
	 * @param path
	 *            the path on which to listen
	 * @param status
	 *            the response HTTP status
	 * @throws JsonProcessingException
	 *             if the given response object could not be serialized
	 */
	private void stubDeleteResponse(Object response, String path)
	        throws JsonProcessingException {
		byte[] body = json.writeValueAsBytes(response);

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(200)
		                        .withHeader("Content-Type", "application/json")
		                        .withBody(body)));
	}

	/**
	 * Helper that sets up WireMock to verify a DELETE request.
	 * 
	 * @param path
	 *            the request path to match
	 */
	private void verifyDeleteRequest(String path) {
		wm.verify(deleteRequestedFor(
		        urlEqualTo(path))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer tok")));
	}

	/**
	 * Helper that sets up WireMock to respond to a POST using a JSON body.
	 * 
	 * @param response
	 *            the response to give, serialized to JSON
	 * @param path
	 *            the path on which to listen
	 * @param status
	 *            the response HTTP status
	 * @throws JsonProcessingException
	 *             if the given response object could not be serialized
	 */
	private void stubPostResponse(Object response, String path, int status)
	        throws JsonProcessingException {
		byte[] body = json.writeValueAsBytes(response);

		wm.stubFor(post(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(status)
		                .withHeader("Content-Type", "application/json")
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

	/**
	 * Helper that sets up WireMock to respond to a POST using a JSON body.
	 * 
	 * @param response
	 *            the response to give, serialized to JSON
	 * @param path
	 *            the path on which to listen
	 * @param status
	 *            the response HTTP status
	 * @throws JsonProcessingException
	 *             if the given response object could not be serialized
	 */
	private void stubPutResponse(Object response, String path, int status)
	        throws JsonProcessingException {
		byte[] body = json.writeValueAsBytes(response);

		wm.stubFor(put(urlEqualTo(path))
		        .willReturn(aResponse()
		                .withStatus(status)
		                .withHeader("Content-Type", "application/json")
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
	private void verifyPutRequest(String path, Object request)
	        throws JsonProcessingException {
		String expectedRequest = json.writeValueAsString(request);

		wm.verify(putRequestedFor(
		        urlEqualTo(path))
		                .withRequestBody(equalToJson(expectedRequest))
		                .withHeader("Content-Type",
		                        matching("application/json; charset=UTF-8"))
		                .withHeader("Accept",
		                        equalTo("application/json; charset=UTF-8"))
		                .withHeader("Authorization", equalTo("Bearer toktok")));
	}

}
