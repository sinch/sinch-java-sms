/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sinch.xms;

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

import com.sinch.xms.api.FeedbackDeliveryCreate;
import com.sinch.xms.api.MtBatchDeliveryFeedbackResult;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.junit.Rule;
import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.sinch.testsupport.TestUtils;
import com.sinch.xms.api.ApiError;
import com.sinch.xms.api.BatchDeliveryReport;
import com.sinch.xms.api.BatchId;
import com.sinch.xms.api.DeliveryStatus;
import com.sinch.xms.api.GroupCreate;
import com.sinch.xms.api.GroupId;
import com.sinch.xms.api.GroupResult;
import com.sinch.xms.api.GroupUpdate;
import com.sinch.xms.api.MoBinarySms;
import com.sinch.xms.api.MoSms;
import com.sinch.xms.api.MoTextSms;
import com.sinch.xms.api.MtBatchBinarySmsCreate;
import com.sinch.xms.api.MtBatchBinarySmsResult;
import com.sinch.xms.api.MtBatchBinarySmsUpdate;
import com.sinch.xms.api.MtBatchDryRunResult;
import com.sinch.xms.api.MtBatchSmsCreate;
import com.sinch.xms.api.MtBatchSmsResult;
import com.sinch.xms.api.MtBatchTextSmsCreate;
import com.sinch.xms.api.MtBatchTextSmsResult;
import com.sinch.xms.api.MtBatchTextSmsUpdate;
import com.sinch.xms.api.Page;
import com.sinch.xms.api.PagedBatchResult;
import com.sinch.xms.api.PagedGroupResult;
import com.sinch.xms.api.PagedInboundsResult;
import com.sinch.xms.api.RecipientDeliveryReport;
import com.sinch.xms.api.Tags;
import com.sinch.xms.api.TagsUpdate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

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

	@Test
	public void canCreateBinaryBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();
		OffsetDateTime time = OffsetDateTime.now(Clock.systemUTC());

		MtBatchBinarySmsCreate request =
		        SinchSMSApi.batchBinarySms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("body".getBytes(TestUtils.US_ASCII))
		                .udh("udh".getBytes(TestUtils.US_ASCII))
		                .build();

		MtBatchBinarySmsResult expected =
		        MtBatchBinarySmsResult.builder()
		                .sender(request.sender())
		                .recipients(request.recipients())
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
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("Hello, world! Здравей свят!")
		                .build();

		MtBatchTextSmsResult expected =
		        MtBatchTextSmsResult.builder()
		                .sender(request.sender())
		                .recipients(request.recipients())
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
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("Hello, ${name}!")
		                .putParameter("name",
		                        SinchSMSApi.parameterValues()
		                                .putSubstitution("123456789", "Jane")
		                                .defaultValue("world")
		                                .build())
		                .build();

		MtBatchTextSmsResult expected =
		        MtBatchTextSmsResult.builder()
		                .sender(request.sender())
		                .recipients(request.recipients())
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
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
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
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
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
	public void canHandleAsyncBatchCreateWithInvalidJson() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		MtBatchTextSmsCreate request =
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
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

		FutureCallback<MtBatchTextSmsResult> callback =
		        new TestCallback<MtBatchTextSmsResult>() {

			        @Override
			        public void failed(Exception e) {
				        assertThat(e,
				                is(instanceOf(JsonParseException.class)));
			        }

		        };

		try {
			conn.createBatchAsync(request, callback).get();
			fail("Expected exception, got none");
		} catch (ExecutionException e) {
			assertThat(e.getCause(), is(instanceOf(JsonParseException.class)));
		} finally {
			conn.close();
		}
	}

	@Test(expected = UnauthorizedException.class)
	public void canHandleBatchCreateWithUnauthorized() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		MtBatchTextSmsCreate request =
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("Hello, world!")
		                .build();

		String path = "/v1/" + spid + "/batches";

		stubPostResponse("", path, HttpStatus.SC_UNAUTHORIZED);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.createBatch(request);
			fail("Expected exception, got none");
		} finally {
			conn.close();
		}
	}

	@Test
	public void canHandleAsyncBatchCreateWithUnauthorized() throws Exception {
		String spid = TestUtils.freshServicePlanId();

		MtBatchTextSmsCreate request =
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("Hello, world!")
		                .build();

		String path = "/v1/" + spid + "/batches";

		stubPostResponse("", path, HttpStatus.SC_UNAUTHORIZED);

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		FutureCallback<MtBatchTextSmsResult> callback =
		        new TestCallback<MtBatchTextSmsResult>() {

			        @Override
			        public void failed(Exception e) {
				        assertThat(e,
				                is(instanceOf(UnauthorizedException.class)));
			        }

		        };

		try {
			conn.createBatchAsync(request, callback).get();
			fail("Expected exception, got none");
		} catch (ExecutionException e) {
			assertThat(e.getCause(),
			        is(instanceOf(UnauthorizedException.class)));
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
		        SinchSMSApi.batchBinarySms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("body".getBytes(TestUtils.US_ASCII))
		                .udh("udh".getBytes(TestUtils.US_ASCII))
		                .build();

		MtBatchBinarySmsResult expected =
		        MtBatchBinarySmsResult.builder()
		                .sender(request.sender())
		                .recipients(request.recipients())
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
			MtBatchBinarySmsResult actual =
			        conn.replaceBatch(batchId, request);
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
		        SinchSMSApi.batchTextSms()
		                .sender("12345")
		                .addRecipient("123456789")
		                .addRecipient("987654321")
		                .body("Hello, world!")
		                .build();

		MtBatchTextSmsResult expected =
		        MtBatchTextSmsResult.builder()
		                .sender(request.sender())
		                .recipients(request.recipients())
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
		        SinchSMSApi.batchTextSmsUpdate()
		                .sender("12345")
		                .body("Hello, world!")
		                .unsetDeliveryReport()
		                .unsetExpireAt()
		                .build();

		MtBatchTextSmsResult expected =
		        MtBatchTextSmsResult.builder()
		                .sender(request.sender())
		                .addRecipient("123")
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
		        SinchSMSApi.batchBinarySmsUpdate()
		                .sender("12345")
		                .body("howdy".getBytes(TestUtils.US_ASCII))
		                .unsetExpireAt()
		                .build();

		MtBatchBinarySmsResult expected =
		        MtBatchBinarySmsResult.builder()
		                .sender(request.sender())
		                .addRecipient("123")
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
		        MtBatchTextSmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
		        MtBatchTextSmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
		        MtBatchBinarySmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
	public void canHandle404WhenFetchingBatchSync() throws Exception {
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
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.fetchBatch(batchId);
			fail("Expected exception, got none");
		} catch (NotFoundException e) {
			assertThat(e.getPath(), is(path));
		} finally {
			conn.close();
		}
	}

	@Test
	public void canHandle404WhenFetchingBatchAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		final String path = "/v1/" + spid + "/batches/" + batchId;

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(404)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())
		                        .withBody("BAD")));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("toktok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		FutureCallback<MtBatchSmsResult> callback =
		        new TestCallback<MtBatchSmsResult>() {

			        @Override
			        public void failed(Exception e) {
				        assertThat(e, is(instanceOf(NotFoundException.class)));
				        NotFoundException nfe = (NotFoundException) e;
				        assertThat(nfe.getPath(), is(path));
			        }

		        };

		try {
			conn.fetchBatchAsync(batchId, callback).get();
			fail("Expected exception, got none");
		} catch (ExecutionException e) {
			assertThat(e.getCause(), is(instanceOf(NotFoundException.class)));
		} finally {
			conn.close();
		}
	}

	@Test
	public void canHandle500WhenFetchingBatch() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId;

		wm.stubFor(get(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(500)
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
		} catch (ExecutionException ee) {
			/*
			 * The exception cause should be the same as we received in the
			 * callback.
			 */
			assertThat(failException.get(), is(theInstance(ee.getCause())));
			assertThat(ee.getCause(),
			        is(instanceOf(UnexpectedResponseException.class)));

			UnexpectedResponseException ure =
			        (UnexpectedResponseException) ee.getCause();

			HttpResponse response = ure.getResponse();
			assertThat(response, notNullValue());
			assertThat(response.getStatusLine().getStatusCode(), is(500));
			assertThat(response.getEntity().getContentType().getValue(),
			        is(ContentType.TEXT_PLAIN.toString()));

			byte[] buf = new byte[100];
			int read;

			InputStream contentStream = null;
			try {
				contentStream = response.getEntity().getContent();
				read = contentStream.read(buf);
			} catch (IOException ioe) {
				throw new AssertionError(
				        "unexpected exception: " + ioe.getMessage(), ioe);
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
		        MtBatchTextSmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
		        MtBatchTextSmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
		        MtBatchBinarySmsResult.builder()
		                .sender("12345")
		                .addRecipient("123456789", "987654321")
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
		BatchFilter filter = SinchSMSApi.batchFilter().build();

		final Page<MtBatchSmsResult> expected =
		        PagedBatchResult.builder()
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
		BatchFilter filter = SinchSMSApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        PagedBatchResult.builder()
		                .page(0)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        PagedBatchResult.builder()
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
		BatchFilter filter = SinchSMSApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        PagedBatchResult.builder()
		                .page(0)
		                .size(1)
		                .totalSize(2)
		                .addContent(
		                        MtBatchTextSmsResult.builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        PagedBatchResult.builder()
		                .page(1)
		                .size(2)
		                .totalSize(2)
		                .addContent(
		                        MtBatchBinarySmsResult.builder()
		                                .id(TestUtils.freshBatchId())
		                                .body((byte) 0)
		                                .udh((byte) 1)
		                                .canceled(false)
		                                .build())
		                .addContent(
		                        MtBatchTextSmsResult.builder()
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
		BatchFilter filter = SinchSMSApi.batchFilter().build();

		// Prepare first page.
		String path1 = "/v1/" + spid + "/batches?page=0";

		final Page<MtBatchSmsResult> expected1 =
		        PagedBatchResult.builder()
		                .page(0)
		                .size(1)
		                .totalSize(3)
		                .addContent(
		                        MtBatchTextSmsResult.builder()
		                                .id(TestUtils.freshBatchId())
		                                .body("body")
		                                .canceled(false)
		                                .build())
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid + "/batches?page=1";

		final Page<MtBatchSmsResult> expected2 =
		        PagedBatchResult.builder()
		                .page(1)
		                .size(2)
		                .totalSize(3)
		                .addContent(
		                        MtBatchBinarySmsResult.builder()
		                                .id(TestUtils.freshBatchId())
		                                .body((byte) 0)
		                                .udh((byte) 1)
		                                .canceled(false)
		                                .build())
		                .addContent(
		                        MtBatchTextSmsResult.builder()
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
		        + "?type=summary&status=Aborted%2CDelivered&code=200%2C300";

		final BatchDeliveryReport expected =
		        BatchDeliveryReport.builder()
		                .batchId(batchId)
		                .totalMessageCount(1010)
		                .addStatus(
		                        BatchDeliveryReport.Status.builder()
		                                .code(200)
		                                .status(DeliveryStatus.ABORTED)
		                                .count(10)
		                                .addRecipient("rec1", "rec2")
		                                .build())
		                .addStatus(
		                        BatchDeliveryReport.Status.builder()
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
		        SinchSMSApi.batchDeliveryReportParams()
		                .summaryReport()
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
		        BatchDeliveryReport.builder()
		                .batchId(batchId)
		                .totalMessageCount(1010)
		                .addStatus(
		                        BatchDeliveryReport.Status.builder()
		                                .code(200)
		                                .status(DeliveryStatus.ABORTED)
		                                .count(10)
		                                .addRecipient("rec1", "rec2")
		                                .build())
		                .addStatus(
		                        BatchDeliveryReport.Status.builder()
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
		        SinchSMSApi.batchDeliveryReportParams()
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
		        RecipientDeliveryReport.builder()
		                .batchId(batchId)
		                .recipient(recipient)
		                .code(200)
		                .status(DeliveryStatus.ABORTED)
		                .statusMessage("this is the status")
		                .operator("10101")
		                .at(OffsetDateTime.now())
		                .operatorStatusAt(
		                        OffsetDateTime.now(Clock.systemUTC()))
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
		        RecipientDeliveryReport.builder()
		                .batchId(batchId)
		                .recipient(recipient)
		                .code(200)
		                .status(DeliveryStatus.ABORTED)
		                .statusMessage("this is the status")
		                .operator("10101")
		                .at(OffsetDateTime.now())
		                .operatorStatusAt(
		                        OffsetDateTime.now(Clock.systemUTC()))
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
	public void canCreateDeliveryFeedback() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/delivery_feedback";

		FeedbackDeliveryCreate request =
		        SinchSMSApi.deliveryFeedback()
						.addRecipient("+15551231234","+15551256344")
				.build();
		ApiConnection conn = ApiConnection.builder()
				.servicePlanId(spid)
				.token("tok")
				.endpoint("http://localhost:" + wm.port())
				.start();
		try {
			conn.createDeliveryFeedback(batchId, request);
		} finally {
			conn.close();
		}
		verifyPostRequest(path, request);
	}

	@Test
	public void canUpdateBatchTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		BatchId batchId = TestUtils.freshBatchId();

		String path = "/v1/" + spid + "/batches/" + batchId + "/tags";

		TagsUpdate request =
		        SinchSMSApi.tagsUpdate()
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
		        SinchSMSApi.tagsUpdate()
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

			Tags actual = conn
			        .replaceTagsAsync(batchId, request, testCallback)
			        .get();
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

		MtBatchSmsCreate request = SinchSMSApi.batchTextSms()
		        .sender("1234")
		        .addRecipient("987654321")
		        .body("Hello, world!")
		        .build();

		final MtBatchDryRunResult expected =
		        MtBatchDryRunResult.builder()
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

		MtBatchSmsCreate request = SinchSMSApi.batchTextSms()
		        .sender("1234")
		        .addRecipient("987654321")
		        .body("Hello, world!")
		        .build();

		final MtBatchDryRunResult expected =
		        MtBatchDryRunResult.builder()
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

			MtBatchDryRunResult actual = conn
			        .createBatchDryRunAsync(request, true, 5, testCallback)
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
		        SinchSMSApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			GroupResult actual = conn.createGroup(request);
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
		        SinchSMSApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		final GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			FutureCallback<GroupResult> testCallback =
			        new TestCallback<GroupResult>() {

				        @Override
				        public void completed(GroupResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResult actual =
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

		GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			GroupResult actual = conn.fetchGroup(groupId);
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

		final GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			FutureCallback<GroupResult> testCallback =
			        new TestCallback<GroupResult>() {

				        @Override
				        public void completed(GroupResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResult actual =
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
		GroupFilter filter = SinchSMSApi.groupFilter().build();

		final Page<GroupResult> expected =
		        PagedGroupResult.builder()
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
			FutureCallback<Page<GroupResult>> testCallback =
			        new TestCallback<Page<GroupResult>>() {

				        @Override
				        public void completed(Page<GroupResult> result) {
					        assertThat(result, is(expected));
				        }

			        };

			PagedFetcher<GroupResult> fetcher = conn.fetchGroups(filter);

			Page<GroupResult> actual =
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
		GroupFilter filter = SinchSMSApi.groupFilter()
		        .addTag("tag1", "таг2")
		        .build();

		// Prepare first page.
		String path1 = "/v1/" + spid
		        + "/groups?page=0&tags=tag1%2C%D1%82%D0%B0%D0%B32";

		final Page<GroupResult> expected1 =
		        PagedGroupResult.builder()
		                .page(0)
		                .size(0)
		                .totalSize(2)
		                .build();

		stubGetResponse(expected1, path1);

		// Prepare second page.
		String path2 = "/v1/" + spid
		        + "/groups?page=1&tags=tag1%2C%D1%82%D0%B0%D0%B32";

		final Page<GroupResult> expected2 =
		        PagedGroupResult.builder()
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
			FutureCallback<Page<GroupResult>> testCallback =
			        new TestCallback<Page<GroupResult>>() {

				        @Override
				        public void completed(Page<GroupResult> result) {
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

			PagedFetcher<GroupResult> fetcher = conn.fetchGroups(filter);

			Page<GroupResult> actual1 =
			        fetcher.fetchAsync(0, testCallback).get();
			assertThat(actual1, is(expected1));

			Page<GroupResult> actual2 =
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

		GroupUpdate request = SinchSMSApi.groupUpdate()
		        .unsetName()
		        .addMemberInsertion("123456789")
		        .addMemberRemoval("987654321")
		        .build();

		GroupResult expected = GroupResult.builder()
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
			GroupResult actual = conn.updateGroup(groupId, request);
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

		GroupUpdate request = SinchSMSApi.groupUpdate()
		        .unsetName()
		        .addMemberInsertion("123456789")
		        .addMemberRemoval("987654321")
		        .build();

		final GroupResult expected = GroupResult.builder()
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
			FutureCallback<GroupResult> testCallback =
			        new TestCallback<GroupResult>() {

				        @Override
				        public void completed(GroupResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResult actual =
			        conn.updateGroupAsync(groupId, request, testCallback)
			                .get();
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
		        SinchSMSApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			GroupResult actual = conn.replaceGroup(groupId, request);
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
		        SinchSMSApi.groupCreate()
		                .name("mygroup")
		                .addMember("123456789")
		                .addMember("987654321", "4242424242")
		                .addTag("tag1", "таг2")
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
		                        .add("kw0", "kw1")
		                        .remove("kw2", "kw3")
		                        .build())
		                .build();

		final GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
			FutureCallback<GroupResult> testCallback =
			        new TestCallback<GroupResult>() {

				        @Override
				        public void completed(GroupResult result) {
					        assertThat(result, is(expected));
				        }

			        };

			GroupResult actual = conn
			        .replaceGroupAsync(groupId, request, testCallback)
			        .get();
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

		GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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

		final GroupResult expected =
		        GroupResult.builder()
		                .id(groupId)
		                .name("mygroup")
		                .size(72)
		                .autoUpdate(SinchSMSApi.autoUpdate()
		                        .recipient("1111")
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
	public void canHandle400WhenDeletingGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();
		String path = "/v1/" + spid + "/groups/" + groupId;

		ApiError apiError = ApiError.of("syntax_constraint_violation",
		        "The syntax constraint was violated");

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(400)
		                        .withHeader("Content-Type", "application/json")
		                        .withBody(json.writeValueAsBytes(apiError))));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.deleteGroup(groupId);
			fail("Expected exception, got none");
		} catch (ErrorResponseException e) {
			assertThat(e.getCode(), is(apiError.code()));
			assertThat(e.getText(), is(apiError.text()));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canHandle401WhenDeletingGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();
		String path = "/v1/" + spid + "/groups/" + groupId;

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(401)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.deleteGroup(groupId);
			fail("Expected exception, got none");
		} catch (UnauthorizedException e) {
			// This exception is expected.
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canHandle401WhenDeletingGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		final String path = "/v1/" + spid + "/groups/" + groupId;

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(401)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		FutureCallback<Void> callback =
		        new TestCallback<Void>() {

			        @Override
			        public void failed(Exception e) {
				        assertThat(e,
				                is(instanceOf(UnauthorizedException.class)));
			        }

		        };

		try {
			conn.deleteGroupAsync(groupId, callback).get();
			fail("Expected exception, got none");
		} catch (ExecutionException e) {
			assertThat(e.getCause(),
			        is(instanceOf(UnauthorizedException.class)));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canHandle404WhenDeletingGroupSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();
		String path = "/v1/" + spid + "/groups/" + groupId;

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(404)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		try {
			conn.deleteGroup(groupId);
			fail("Expected exception, got none");
		} catch (NotFoundException e) {
			assertThat(e.getPath(), is(path));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canHandle404WhenDeletingGroupAsync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		final String path = "/v1/" + spid + "/groups/" + groupId;

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(404)
		                        .withHeader("Content-Type",
		                                ContentType.TEXT_PLAIN.toString())));

		ApiConnection conn = ApiConnection.builder()
		        .servicePlanId(spid)
		        .token("tok")
		        .endpoint("http://localhost:" + wm.port())
		        .start();

		FutureCallback<Void> callback =
		        new TestCallback<Void>() {

			        @Override
			        public void failed(Exception e) {
				        assertThat(e, is(instanceOf(NotFoundException.class)));
				        NotFoundException nfe = (NotFoundException) e;
				        assertThat(nfe.getPath(), is(path));
			        }

		        };

		try {
			conn.deleteGroupAsync(groupId, callback).get();
			fail("Expected exception, got none");
		} catch (ExecutionException e) {
			assertThat(e.getCause(), is(instanceOf(NotFoundException.class)));
		} finally {
			conn.close();
		}

		verifyDeleteRequest(path);
	}

	@Test
	public void canHandle500WhenDeletingGroup() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId;

		wm.stubFor(delete(
		        urlEqualTo(path))
		                .willReturn(aResponse()
		                        .withStatus(500)
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

			FutureCallback<Void> testCallback =
			        new TestCallback<Void>() {

				        @Override
				        public void failed(Exception exception) {
					        if (!failException.compareAndSet(null,
					                exception)) {
						        fail("failed called multiple times");
					        }

					        latch.countDown();
				        }

			        };

			Future<Void> future =
			        conn.deleteGroupAsync(groupId, testCallback);

			// Give plenty of time for the callback to be called.
			latch.await();

			future.get();
			fail("unexpected future get success");
		} catch (ExecutionException ee) {
			/*
			 * The exception cause should be the same as we received in the
			 * callback.
			 */
			assertThat(failException.get(), is(theInstance(ee.getCause())));
			assertThat(ee.getCause(),
			        is(instanceOf(UnexpectedResponseException.class)));

			UnexpectedResponseException ure =
			        (UnexpectedResponseException) ee.getCause();

			HttpResponse response = ure.getResponse();
			assertThat(response, notNullValue());
			assertThat(response.getStatusLine().getStatusCode(), is(500));
			assertThat(response.getEntity().getContentType().getValue(),
			        is(ContentType.TEXT_PLAIN.toString()));

			byte[] buf = new byte[100];
			int read;

			InputStream contentStream = null;
			try {
				contentStream = response.getEntity().getContent();
				read = contentStream.read(buf);
			} catch (IOException ioe) {
				throw new AssertionError(
				        "unexpected exception: " + ioe.getMessage(), ioe);
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

		verifyDeleteRequest(path);
	}

	@Test
	public void canUpdateGroupTagsSync() throws Exception {
		String spid = TestUtils.freshServicePlanId();
		GroupId groupId = TestUtils.freshGroupId();

		String path = "/v1/" + spid + "/groups/" + groupId + "/tags";

		TagsUpdate request =
		        SinchSMSApi.tagsUpdate()
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
		        SinchSMSApi.tagsUpdate()
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

			Tags actual = conn
			        .replaceTagsAsync(groupId, request, testCallback)
			        .get();
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
		InboundsFilter filter = SinchSMSApi.inboundsFilter().build();

		final Page<MoSms> expected =
		        PagedInboundsResult.builder()
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
		InboundsFilter filter = SinchSMSApi.inboundsFilter()
		        .addRecipient("10101")
		        .build();
		String inboundsId1 = TestUtils.freshSmsId();
		String inboundsId2 = TestUtils.freshSmsId();
		OffsetDateTime time1 = OffsetDateTime.now(Clock.systemUTC());
		OffsetDateTime time2 = OffsetDateTime.now(Clock.systemUTC());

		// Prepare first page.
		String path1 = "/v1/" + spid + "/inbounds?page=0&to=10101";

		final Page<MoSms> expected1 =
		        PagedInboundsResult.builder()
		                .page(0)
		                .size(1)
		                .totalSize(2)
		                .addContent(MoTextSms.builder()
		                        .sender("987654321")
		                        .recipient("54321")
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
		        PagedInboundsResult.builder()
		                .page(1)
		                .size(1)
		                .totalSize(2)
		                .addContent(MoBinarySms.builder()
		                        .sender("123456789")
		                        .recipient("12345")
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

		MoSms expected = MoBinarySms.builder()
		        .sender("123456789")
		        .recipient("12345")
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

		final MoSms expected = MoBinarySms.builder()
		        .sender("123456789")
		        .recipient("12345")
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
		                .withHeader("Authorization",
		                        equalTo("Bearer toktok")));
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
		                .withHeader("Authorization",
		                        equalTo("Bearer toktok")));
	}

}
