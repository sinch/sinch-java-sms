package com.clxcommunications.xms;

/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clxcommunications.xms.api.BatchDeliveryReport;
import com.clxcommunications.xms.api.BatchId;
import com.clxcommunications.xms.api.GroupCreate;
import com.clxcommunications.xms.api.GroupId;
import com.clxcommunications.xms.api.GroupResult;
import com.clxcommunications.xms.api.GroupUpdate;
import com.clxcommunications.xms.api.MoSms;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * An abstract representation of an XMS connection. This class exposes a number
 * of methods which can be called to interact with the XMS REST API.
 * <p>
 * To instantiate this class it is necessary to use a builder, see
 * {@link #builder()}. The builder can be used to configure the connection as
 * necessary, once the connection is opened with {@link Builder#start()} or
 * {@link #start()} it is necessary to later close the connection using
 * {@link #close()}.
 */
@Value.Immutable(copy = false)
@ValueStylePackageDirect
public abstract class ApiConnection implements Closeable {

	/**
	 * A builder of API connections. At a minimum the service plan identifier
	 * and authentication token must be set.
	 */
	public static class Builder extends ApiConnectionImpl.Builder {

		/**
		 * Initializes the endpoint from the given URL string. The URL should
		 * not contain query or fragment components.
		 * 
		 * @param url
		 *            the URL to the XMS endpoint
		 * @return this builder for use in a chained invocation
		 */
		public Builder endpoint(String url) {
			return this.endpoint(URI.create(url));
		}

		/**
		 * Builds a new, initially stopped, {@link ApiConnection}. Before
		 * attempting to interact with XMS it is necessary to start the returned
		 * connection, this is done using {@link ApiConnection#start()}.
		 * <p>
		 * The {@link #start()} method is recommended for the common case where
		 * the built {@link ApiConnection} is immediately started.
		 * 
		 * @return a freshly built API connection
		 * @throws java.lang.IllegalStateException
		 *             if any required attributes are missing
		 */
		@Override
		public ApiConnection build() {
			return super.build();
		}

		/**
		 * Builds and starts the defined API connection. This is identical to
		 * calling {@link #build()} and then immediately calling
		 * {@link ApiConnection#start()} on the generated connection object.
		 * 
		 * @return an API connection
		 */
		public ApiConnection start() {
			ApiConnection conn = build();

			conn.start();

			return conn;
		}

	}

	private static final Logger log =
	        LoggerFactory.getLogger(ApiConnection.class);

	/**
	 * The default endpoint of this API connection.
	 */
	public static final URI DEFAULT_ENDPOINT =
	        URI.create("https://api.clxcommunications.com/xms");

	/**
	 * A Jackson object mapper.
	 */
	private final ApiObjectMapper json;

	/**
	 * Constructor of API connections. This only has package visibility since
	 * users of the SDK are not expected to inherit from this class.
	 */
	ApiConnection() {
		json = new ApiObjectMapper();
	}

	/**
	 * Returns a fresh builder of API connections.
	 * 
	 * @return a non-null connection builder
	 */
	@Nonnull
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Starts up this API connection. This must be called before performing any
	 * API calls.
	 */
	public void start() {
		log.debug("Starting API connection: {}", this);

		if (httpClient() instanceof ApiDefaultHttpAsyncClient) {
			((ApiDefaultHttpAsyncClient) httpClient()).start();
		} else {
			log.debug("Not starting HTTP client since it was given externally");
		}
	}

	/**
	 * Closes this API connection and releases associated resources.
	 * <p>
	 * Note, this will <em>not</em> shut down the HTTP client if the API
	 * connection was initialized with a custom {@link HttpAsyncClient http
	 * client}.
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		log.debug("Closing API connection: {}", this);

		HttpAsyncClient c = httpClient();
		if (c instanceof ApiDefaultHttpAsyncClient
		        && ((ApiDefaultHttpAsyncClient) c).isStartedInternally()) {
			((ApiDefaultHttpAsyncClient) c).close();
		} else {
			log.debug("Not closing HTTP client since it was given externally");
		}
	}

	/**
	 * The XMS authentication token.
	 * 
	 * @return a non-null string
	 */
	public abstract String token();

	/**
	 * The XMS service plan identifier.
	 * 
	 * @return a non-null string
	 */
	public abstract String servicePlanId();

	/**
	 * Whether the JSON sent to the server should be printed with indentation.
	 * Default is to <i>not</i> pretty print.
	 * 
	 * @return true if pretty printing is enabled; false otherwise
	 */
	@Value.Default
	public boolean prettyPrintJson() {
		return false;
	}

	/**
	 * The HTTP client used by this connection. The default client is a minimal
	 * one that does not support, for example, authentication or redirects.
	 * <p>
	 * Note, when this API connection is closed then this HTTP client is also
	 * closed <em>only</em> if the default HTTP client is used. That is, if
	 * {@link Builder#httpClient(HttpAsyncClient)} was used to initialize using
	 * an external {@link HttpAsyncClient} then this client must also be started
	 * up and shut down externally.
	 * 
	 * @return a non-null HTTP client
	 */
	@Value.Default
	public HttpAsyncClient httpClient() {
		return new ApiDefaultHttpAsyncClient(true);
	}

	/**
	 * The future callback wrapper to use in all API calls. By default this is
	 * {@link CallbackWrapper#exceptionDropper}, that is, any exception thrown
	 * in a given callback is logged and dropped.
	 * 
	 * @return a non-null callback wrapper
	 */
	@Value.Default
	public CallbackWrapper callbackWrapper() {
		return CallbackWrapper.exceptionDropper;
	}

	/**
	 * The base endpoint of the XMS API. This specifies the HTTP host and base
	 * path that will be used in sending requests to XMS. The URL should not
	 * contain query or fragment components.
	 * 
	 * @return a non-null URL
	 */
	@Value.Default
	public URI endpoint() {
		return DEFAULT_ENDPOINT;
	}

	/**
	 * The HTTP host providing the XMS API.
	 * 
	 * @return a non-null host specification
	 */
	@Value.Derived
	protected HttpHost endpointHost() {
		URI uri = endpoint();
		return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
	}

	/**
	 * Validates that this object is in a coherent state.
	 */
	@Value.Check
	protected void check() {
		json.configure(SerializationFeature.INDENT_OUTPUT,
		        prettyPrintJson());

		if (endpoint().getQuery() != null) {
			throw new IllegalStateException(
			        "base endpoint has query component");
		}

		if (endpoint().getFragment() != null) {
			throw new IllegalStateException(
			        "base endpoint has fragment component");
		}

		/*
		 * Attempt to create a plain endpoint URL. If it succeeds then all
		 * endpoints generated in normal use of this class should succeed.
		 * 
		 * Note, this does not mean that the generated URL makes sense, it only
		 * means that the code will not throw exceptions.
		 */
		endpoint("");
	}

	/**
	 * Helper returning an endpoint URL for the given sub-path and query
	 * parameters.
	 * 
	 * @param subPath
	 *            path fragment to place after the base path
	 * @param params
	 *            the query parameters, may be empty
	 * @return a non-null endpoint URL
	 */
	@Nonnull
	private URI endpoint(@Nonnull String subPath,
	        @Nonnull List<NameValuePair> params) {
		try {
			String spid = URLEncoder.encode(servicePlanId(), "UTF-8");
			String path = endpoint().getPath() + "/v1/" + spid + subPath;
			URIBuilder uriBuilder = new URIBuilder(endpoint())
			        .setPath(path);

			if (!params.isEmpty()) {
				uriBuilder.setParameters(params);
			}

			return uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Like {@link #endpoint(String, String)} but with no query parameters.
	 * 
	 * @param subPath
	 *            path fragment to place after the base path
	 * @return a non-null endpoint URL
	 * @throws IllegalArgumentException
	 *             if the generated URL is invalid, wraps the
	 *             {@link URISyntaxException}
	 */
	@Nonnull
	private URI endpoint(String subPath) {
		return endpoint(subPath, Collections.<NameValuePair> emptyList());
	}

	@Nonnull
	private URI batchesEndpoint() {
		return endpoint("/batches", Collections.<NameValuePair> emptyList());
	}

	@Nonnull
	private URI batchEndpoint(BatchId batchId) {
		return endpoint("/batches/" + batchId);
	}

	@Nonnull
	private URI batchDeliveryReportEndpoint(BatchId batchId,
	        List<NameValuePair> params) {
		return endpoint(
		        "/batches/" + batchId + "/delivery_report",
		        params);
	}

	@Nonnull
	private URI batchDryRunEndpoint(List<NameValuePair> params) {
		return endpoint("/batches/dry_run", params);
	}

	@Nonnull
	private URI batchRecipientDeliveryReportEndpoint(BatchId batchId,
	        String recipient) {
		return endpoint(
		        "/batches/" + batchId + "/delivery_report/" + recipient);
	}

	@Nonnull
	private URI batchTagsEndpoint(BatchId batchId) {
		return endpoint("/batches/" + batchId + "/tags");
	}

	@Nonnull
	private URI groupsEndpoint() {
		return endpoint("/groups");
	}

	@Nonnull
	private URI groupsEndpoint(List<NameValuePair> params) {
		return endpoint("/groups", params);
	}

	@Nonnull
	private URI groupEndpoint(GroupId id) {
		return endpoint("/groups/" + id);
	}

	@Nonnull
	private URI groupMembersEndpoint(GroupId id) {
		return endpoint("/groups/" + id + "/members");
	}

	@Nonnull
	private URI groupTagsEndpoint(GroupId id) {
		return endpoint("/groups/" + id + "/tags");
	}

	@Nonnull
	private URI inboundsEndpoint(List<NameValuePair> params) {
		return endpoint("/inbounds", params);
	}

	@Nonnull
	private URI inboundEndpoint(String id) {
		return endpoint("/inbounds/" + id);
	}

	/**
	 * Helper that produces a HTTP consumer that consumes the given class as a
	 * JSON object. The generics stuff here is to get a form of covariant
	 * relation between the type of JSON input and the return type of this
	 * class. Basically, if `P extends T` then it should be possible to read a
	 * JSON type `P` but instead of necessarily returning a value of type `P`
	 * return it as a `T`.
	 * 
	 * @param clazz
	 *            the class whose JSON representation is consumed
	 * @return an HTTP consumer
	 */
	@SuppressWarnings("unchecked")
	private <T, P extends T> JsonApiAsyncConsumer<T> jsonAsyncConsumer(
	        Class<P> clazz) {
		return (JsonApiAsyncConsumer<T>) new JsonApiAsyncConsumer<P>(json,
		        clazz);
	}

	/**
	 * POSTs a JSON serialization of the given object to the given endpoint.
	 * 
	 * @param endpoint
	 *            the target endpoint
	 * @param object
	 *            the object whose JSON representation is sent
	 * @return a HTTP post request
	 */
	private <T> HttpPost post(URI endpoint, T object) {
		return withJsonContent(object,
		        withStandardHeaders(new HttpPost(endpoint)));
	}

	/**
	 * PUTs a JSON serialization of the given object to the given endpoint.
	 * 
	 * @param endpoint
	 *            the target endpoint
	 * @param object
	 *            the object whose JSON representation is sent
	 * @return a HTTP put request
	 */
	private <T> HttpPut put(URI endpoint, T object) {
		return withJsonContent(object,
		        withStandardHeaders(new HttpPut(endpoint)));
	}

	/**
	 * GETs from the given endpoint.
	 * 
	 * @param endpoint
	 *            the target endpoint
	 * @return a HTTP get request
	 */
	private HttpGet get(URI endpoint) {
		return withStandardHeaders(new HttpGet(endpoint));
	}

	/**
	 * DELETEs from the given endpoint.
	 * 
	 * @param endpoint
	 *            the target endpoint
	 * @return a HTTP delete request
	 */
	private HttpDelete delete(URI endpoint) {
		return withStandardHeaders(new HttpDelete(endpoint));
	}

	/**
	 * Attaches the headers that XMS require.
	 * 
	 * @param req
	 *            the request to which the headers should be added
	 * @return the given request object
	 */
	private <T extends HttpRequest> T withStandardHeaders(T req) {
		req.setHeader("Authorization", "Bearer " + token());
		req.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
		req.setHeader("X-CLX-SDK-Version", Version.VERSION);
		return req;
	}

	/**
	 * Attaches an object serialized as JSON to the given request.
	 * 
	 * @param object
	 *            the object that should be serialized and added to the request
	 * @param req
	 *            the request to which the headers should be added
	 * @return the given request object
	 */
	private <T extends HttpEntityEnclosingRequest> T withJsonContent(
	        Object object, T req) {
		final byte[] content;

		/*
		 * Attempt to serialize the given object into JSON. Note, we wrap the
		 * JsonProcessingException in a runtime exception since we control which
		 * objects will be serialized and can guarantee that they all should be
		 * serializable. Thus, if the exception still is thrown it indicates a
		 * severe bug in internal state management.
		 */
		try {
			content = json.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}

		ByteArrayEntity entity =
		        new ByteArrayEntity(content, ContentType.APPLICATION_JSON);

		req.setEntity(entity);

		return req;
	}

	/**
	 * Creates the given batch and schedules it for submission. If
	 * {@link MtBatchTextSmsCreate#sendAt()} returns <code>null</code> then the
	 * batch submission will begin immediately.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #createBatchAsync(MtBatchTextSmsCreate, FutureCallback)} instead.
	 * 
	 * @param sms
	 *            the batch to create
	 * @return a batch creation result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchTextSmsResult createBatch(MtBatchTextSmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return createBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously creates the given text batch and schedules it for
	 * submission. If {@link MtBatchTextSmsCreate#sendAt()} returns
	 * <code>null</code> then the batch submission will begin immediately.
	 * 
	 * @param sms
	 *            the batch to create
	 * @param callback
	 *            a callback that is invoked when batch is created
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchTextSmsResult> createBatchAsync(
	        MtBatchTextSmsCreate sms,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpPost req = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Creates the given batch and schedules it for submission. If
	 * {@link MtBatchBinarySmsCreate#sendAt()} returns <code>null</code> then
	 * the batch submission will begin immediately.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #createBatchAsync(MtBatchBinarySmsCreate, FutureCallback)}
	 * instead.
	 * 
	 * @param sms
	 *            the batch to create
	 * @return a batch creation result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchBinarySmsResult createBatch(MtBatchBinarySmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return createBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously creates the given binary batch and schedules it for
	 * submission. If {@link MtBatchBinarySmsCreate#sendAt()} returns
	 * <code>null</code> then the batch submission will begin immediately.
	 * 
	 * @param sms
	 *            the batch to create
	 * @param callback
	 *            a callback that is invoked when batch is created
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchBinarySmsResult> createBatchAsync(
	        MtBatchBinarySmsCreate sms,
	        FutureCallback<MtBatchBinarySmsResult> callback) {
		HttpPost req = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Replaces the batch with the given identifier. After this method completes
	 * the batch will match the provided batch description.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #replaceBatchAsync(BatchId, MtBatchTextSmsCreate, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch to replace
	 * @param sms
	 *            the batch description
	 * @return a batch submission result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchTextSmsResult replaceBatch(BatchId id,
	        MtBatchTextSmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return replaceBatchAsync(id, sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously replaces the batch with the given identifier. On
	 * completion, the batch will match the provided batch description.
	 * 
	 * @param id
	 *            identifier of the batch to replace
	 * @param sms
	 *            the new batch description
	 * @param callback
	 *            a callback that is invoked when replace is completed
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchTextSmsResult> replaceBatchAsync(BatchId id,
	        MtBatchTextSmsCreate sms,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpPut req = put(batchEndpoint(id), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Replaces the batch with the given identifier. After this method completes
	 * the batch will match the provided batch description.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #replaceBatchAsync(BatchId, MtBatchBinarySmsCreate, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch to replace
	 * @param sms
	 *            the batch description
	 * @return a batch submission result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchBinarySmsResult replaceBatch(BatchId id,
	        MtBatchBinarySmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return replaceBatchAsync(id, sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously replaces the batch with the given identifier. On
	 * completion, the batch will match the provided batch description.
	 * 
	 * @param id
	 *            identifier of the batch to replace
	 * @param sms
	 *            the new batch description
	 * @param callback
	 *            a callback that is invoked when replace is completed
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchBinarySmsResult> replaceBatchAsync(BatchId id,
	        MtBatchBinarySmsCreate sms,
	        FutureCallback<MtBatchBinarySmsResult> callback) {
		HttpPut req = put(batchEndpoint(id), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the given text batch.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #updateBatchAsync(BatchId, MtBatchTextSmsUpdate, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch to update
	 * @param sms
	 *            a description of the desired updated
	 * @return the batch with the updates applied
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchTextSmsResult updateBatch(BatchId id,
	        MtBatchTextSmsUpdate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return updateBatchAsync(id, sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously updates the text batch with the given batch ID. The batch
	 * is updated to match the given update object.
	 * 
	 * @param batchId
	 *            the batch that should be updated
	 * @param sms
	 *            description of the desired update
	 * @param callback
	 *            called at call success, failure, or cancellation
	 * @return a future containing the updated batch
	 */
	public Future<MtBatchTextSmsResult> updateBatchAsync(BatchId batchId,
	        MtBatchTextSmsUpdate sms,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpPost req = post(batchEndpoint(batchId), sms);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> consumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the given binary batch.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #updateBatchAsync(BatchId, MtBatchBinarySmsUpdate, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch to update
	 * @param sms
	 *            a description of the desired updated
	 * @return the batch with the updates applied
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchBinarySmsResult updateBatch(BatchId id,
	        MtBatchBinarySmsUpdate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return updateBatchAsync(id, sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously updates the binary batch with the given batch ID. The
	 * batch is updated to match the given update object.
	 * 
	 * @param batchId
	 *            the batch that should be updated
	 * @param sms
	 *            description of the desired update
	 * @param callback
	 *            called at call success, failure, or cancellation
	 * @return a future containing the updated batch
	 */
	public Future<MtBatchBinarySmsResult> updateBatchAsync(BatchId batchId,
	        MtBatchBinarySmsUpdate sms,
	        FutureCallback<MtBatchBinarySmsResult> callback) {
		HttpPost req = post(batchEndpoint(batchId), sms);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> consumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches the given batch.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchBatchAsync(BatchId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the batch to fetch
	 * @return the desired batch
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchSmsResult fetchBatch(BatchId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchBatchAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches a batch with the given batch ID.
	 * 
	 * @param batchId
	 *            ID of the batch to fetch
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the desired batch
	 */
	public Future<MtBatchSmsResult> fetchBatchAsync(BatchId batchId,
	        FutureCallback<MtBatchSmsResult> callback) {
		HttpGet req = get(batchEndpoint(batchId));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<MtBatchSmsResult> consumer =
		        jsonAsyncConsumer(MtBatchSmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Creates a page fetcher to retrieve a paged list of batches. Note, this
	 * method does not itself cause any network activity.
	 * 
	 * @param filter
	 *            the batch filter
	 * @return a future page
	 */
	public PagedFetcher<MtBatchSmsResult> fetchBatches(
	        final BatchFilter filter) {
		return new PagedFetcher<MtBatchSmsResult>() {

			@Override
			Future<Page<MtBatchSmsResult>> fetchAsync(int page,
			        FutureCallback<Page<MtBatchSmsResult>> callback) {
				return fetchBatches(page, filter,
				        callbackWrapper().wrap(callback));
			}

		};
	}

	/**
	 * Fetches the given page in a paged list of batches.
	 * 
	 * @param page
	 *            the page to fetch
	 * @param filter
	 *            the batch filter
	 * @param callback
	 *            the callback to invoke when call is finished
	 * @return a future page
	 */
	private Future<Page<MtBatchSmsResult>> fetchBatches(int page,
	        BatchFilter filter,
	        FutureCallback<Page<MtBatchSmsResult>> callback) {
		List<NameValuePair> params = filter.toQueryParams(page);
		URI url = endpoint("/batches", params);

		HttpGet req = get(url);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Page<MtBatchSmsResult>> consumer =
		        jsonAsyncConsumer(PagedBatchResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Cancels the batch with the given batch ID.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #cancelBatchAsync(BatchId, FutureCallback)} instead.
	 * 
	 * @param batchId
	 *            identifier of the batch to delete
	 * @return the cancelled batch
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchSmsResult cancelBatch(BatchId batchId)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return cancelBatchAsync(batchId, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Cancels the batch with the given batch ID.
	 * 
	 * @param batchId
	 *            identifier of the batch to delete
	 * @param callback
	 *            the callback invoked when request completes
	 * @return a future containing the batch that was cancelled
	 */
	public Future<MtBatchSmsResult> cancelBatchAsync(BatchId batchId,
	        FutureCallback<MtBatchSmsResult> callback) {
		HttpDelete req = delete(batchEndpoint(batchId));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<MtBatchSmsResult> consumer =
		        jsonAsyncConsumer(MtBatchSmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Attempts to perform a dry run of the given batch.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #createBatchDryRunAsync(MtBatchSmsCreate, Boolean, Integer, FutureCallback)}
	 * instead.
	 * 
	 * @param sms
	 *            the batch to dry run
	 * @param perRecipient
	 *            whether the per-recipient result should be populated
	 * @param numRecipients
	 *            the number of recipients to populate
	 * @return a dry run result
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MtBatchDryRunResult createBatchDryRun(MtBatchSmsCreate sms,
	        Boolean perRecipient, Integer numRecipients)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return createBatchDryRunAsync(sms, perRecipient, numRecipients,
			        null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously performs a dry run of the given batch.
	 * 
	 * @param sms
	 *            the batch to dry run
	 * @param perRecipient
	 *            whether the per-recipient result should be populated
	 * @param numRecipients
	 *            the number of recipients to populate
	 * @param callback
	 *            a callback that is invoked when dry run is complete
	 * @return a future whose result is the dry run result
	 */
	public Future<MtBatchDryRunResult> createBatchDryRunAsync(
	        MtBatchSmsCreate sms, Boolean perRecipient, Integer numRecipients,
	        FutureCallback<MtBatchDryRunResult> callback) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);

		if (perRecipient != null) {
			params.add(new BasicNameValuePair("per_recipient",
			        perRecipient.toString()));
		}

		if (numRecipients != null) {
			params.add(new BasicNameValuePair("number_of_recipients",
			        numRecipients.toString()));
		}

		HttpPost req = post(batchDryRunEndpoint(params), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<MtBatchDryRunResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchDryRunResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches a delivery report for the batch with the given batch ID.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchDeliveryReportAsync(BatchId, BatchDeliveryReportParams, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch whose delivery report to fetch
	 * @param filter
	 *            parameters controlling the response content
	 * @return the desired delivery report
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public BatchDeliveryReport fetchDeliveryReport(BatchId id,
	        BatchDeliveryReportParams filter)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchDeliveryReportAsync(id, filter, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches a delivery report for the batch with the given batch ID.
	 * 
	 * @param id
	 *            batch ID of the delivery report batch to fetch
	 * @param filter
	 *            parameters controlling the response content
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the delivery report
	 */
	public Future<BatchDeliveryReport> fetchDeliveryReportAsync(BatchId id,
	        BatchDeliveryReportParams filter,
	        FutureCallback<BatchDeliveryReport> callback) {
		List<NameValuePair> params = filter.toQueryParams();
		HttpGet req = get(batchDeliveryReportEndpoint(id, params));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<BatchDeliveryReport> consumer =
		        jsonAsyncConsumer(BatchDeliveryReport.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches a delivery report for the batch with the given batch ID and
	 * recipient.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchDeliveryReportAsync(BatchId, String, FutureCallback)}
	 * instead.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param recipient
	 *            MSISDN of recipient
	 * @return the desired delivery report
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public RecipientDeliveryReport fetchDeliveryReport(BatchId id,
	        String recipient) throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchDeliveryReportAsync(id, recipient, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches a delivery report for the batch with the given batch ID and
	 * recipient.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param recipient
	 *            MSISDN of recipient
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the delivery report
	 */
	public Future<RecipientDeliveryReport> fetchDeliveryReportAsync(BatchId id,
	        String recipient,
	        FutureCallback<RecipientDeliveryReport> callback) {
		HttpGet req = get(batchRecipientDeliveryReportEndpoint(id, recipient));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<RecipientDeliveryReport> consumer =
		        jsonAsyncConsumer(RecipientDeliveryReport.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the tags of the batch with the given batch ID.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #updateTagsAsync(BatchId, TagsUpdate, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param tags
	 *            the tag update object
	 * @return the updated set of tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags updateTags(BatchId id, TagsUpdate tags)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return updateTagsAsync(id, tags, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Updates the tags of the batch with the given batch ID.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param tags
	 *            the tag update object
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the updated set of tags
	 */
	public Future<Tags> updateTagsAsync(BatchId id, TagsUpdate tags,
	        FutureCallback<Tags> callback) {
		HttpPost req = post(batchTagsEndpoint(id), tags);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Replaces the tags of the batch with the given batch ID.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #replaceTagsAsync(BatchId, Tags, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param tags
	 *            the replacements tags
	 * @return the new set of tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags replaceTags(BatchId id, Tags tags)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return replaceTagsAsync(id, tags, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Replaces the tags of the batch with the given batch ID.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param tags
	 *            the replacement tags
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the new set of tags
	 */
	public Future<Tags> replaceTagsAsync(BatchId id, Tags tags,
	        FutureCallback<Tags> callback) {
		HttpPut req = put(batchTagsEndpoint(id), tags);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches the tags of the batch with the given batch ID.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchTagsAsync(BatchId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @return the batch tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags fetchTags(BatchId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchTagsAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches the tags of the batch with the given batch ID.
	 * 
	 * @param id
	 *            identifier of the batch
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the new set of tags
	 */
	public Future<Tags> fetchTagsAsync(BatchId id,
	        FutureCallback<Tags> callback) {
		HttpGet req = get(batchTagsEndpoint(id));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Attempts to create the given group synchronously.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #createGroupAsync(GroupCreate, FutureCallback)} instead.
	 * 
	 * @param group
	 *            the group to create
	 * @return a created group
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public GroupResult createGroup(GroupCreate group)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return createGroupAsync(group, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously creates the given group.
	 * 
	 * @param group
	 *            the group to create
	 * @param callback
	 *            a callback that is invoked when creation is completed
	 * @return a future whose result is the creation response
	 */
	public Future<GroupResult> createGroupAsync(GroupCreate group,
	        FutureCallback<GroupResult> callback) {
		HttpPost req = post(groupsEndpoint(), group);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<GroupResult> responseConsumer =
		        jsonAsyncConsumer(GroupResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Attempts to fetch the given group.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchGroupAsync(GroupId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            the group to fetch
	 * @return the fetched group
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public GroupResult fetchGroup(GroupId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchGroupAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously fetches the given group.
	 * 
	 * @param id
	 *            the group to fetch
	 * @param callback
	 *            a callback that is invoked when fetching is completed
	 * @return a future whose result is the fetch response
	 */
	public Future<GroupResult> fetchGroupAsync(GroupId id,
	        FutureCallback<GroupResult> callback) {
		HttpGet req = get(groupEndpoint(id));

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<GroupResult> responseConsumer =
		        jsonAsyncConsumer(GroupResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Attempts to fetch the members of the given group synchronously.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchGroupMembersAsync(GroupId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            the group whose members should be fetched
	 * @return the fetched members
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Set<String> fetchGroupMembers(GroupId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchGroupMembersAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously fetches the members of the given group.
	 * 
	 * @param id
	 *            the group whose members should be fetched
	 * @param callback
	 *            a callback that is invoked when fetching is completed
	 * @return a future whose result is a set of group members
	 */
	public Future<Set<String>> fetchGroupMembersAsync(GroupId id,
	        FutureCallback<Set<String>> callback) {
		HttpGet req = get(groupMembersEndpoint(id));

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		@SuppressWarnings("unchecked")
		HttpAsyncResponseConsumer<Set<String>> responseConsumer =
		        jsonAsyncConsumer(Set.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Creates a page fetcher to retrieve a paged list of groups. Note, this
	 * method does not itself cause any network activity.
	 * 
	 * @param filter
	 *            the group filter
	 * @return a future page
	 */
	public PagedFetcher<GroupResult> fetchGroups(
	        final GroupFilter filter) {
		return new PagedFetcher<GroupResult>() {

			@Override
			Future<Page<GroupResult>> fetchAsync(int page,
			        FutureCallback<Page<GroupResult>> callback) {
				return fetchGroups(page, filter,
				        callbackWrapper().wrap(callback));
			}

		};
	}

	/**
	 * Fetches the given page in a paged list of groups.
	 * 
	 * @param page
	 *            the page to fetch
	 * @param filter
	 *            the group filter
	 * @param callback
	 *            the callback to invoke when call is finished
	 * @return a future page
	 */
	private Future<Page<GroupResult>> fetchGroups(int page,
	        GroupFilter filter,
	        FutureCallback<Page<GroupResult>> callback) {
		List<NameValuePair> params = filter.toQueryParams(page);
		HttpGet req = get(groupsEndpoint(params));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Page<GroupResult>> consumer =
		        jsonAsyncConsumer(PagedGroupResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the given group.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #updateGroupAsync(GroupId, GroupUpdate, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group to update
	 * @param group
	 *            a description of the desired updates
	 * @return the group with the updates applied
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public GroupResult updateGroup(GroupId id, GroupUpdate group)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return updateGroupAsync(id, group, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously updates the group with the given group ID. The group is
	 * updated to match the given update object.
	 * 
	 * @param id
	 *            the group that should be updated
	 * @param group
	 *            description of the desired updates
	 * @param callback
	 *            called at call success, failure, or cancellation
	 * @return a future containing the updated group
	 */
	public Future<GroupResult> updateGroupAsync(GroupId id, GroupUpdate group,
	        FutureCallback<GroupResult> callback) {
		HttpPost req = post(groupEndpoint(id), group);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<GroupResult> consumer =
		        jsonAsyncConsumer(GroupResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Replaces the given group.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #replaceGroupAsync(GroupId, GroupCreate, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group to replace
	 * @param group
	 *            a description of the replacement group
	 * @return the new group
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public GroupResult replaceGroup(GroupId id, GroupCreate group)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return replaceGroupAsync(id, group, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously replaces the group with the given group ID. The group is
	 * replaced by the given group definition.
	 * 
	 * @param id
	 *            the group that should be replaced
	 * @param group
	 *            description of the new group
	 * @param callback
	 *            called at call success, failure, or cancellation
	 * @return a future containing the new group
	 */
	public Future<GroupResult> replaceGroupAsync(GroupId id,
	        GroupCreate group, FutureCallback<GroupResult> callback) {
		HttpPut req = put(groupEndpoint(id), group);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<GroupResult> consumer =
		        jsonAsyncConsumer(GroupResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Deletes the given group.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #deleteGroupAsync(GroupId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group to delete
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public void deleteGroup(GroupId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			deleteGroupAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously deletes the group with the given group ID.
	 * 
	 * @param id
	 *            the group that should be deleted
	 * @param callback
	 *            called at call success, failure, or cancellation
	 * @return a future containing nothing
	 */
	public Future<Void> deleteGroupAsync(GroupId id,
	        FutureCallback<Void> callback) {
		HttpDelete req = delete(groupEndpoint(id));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);
		HttpAsyncResponseConsumer<Void> consumer = new EmptyAsyncConsumer(json);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the tags of the group with the given identifier.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #updateTagsAsync(GroupId, TagsUpdate, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group
	 * @param tags
	 *            the tag update object
	 * @return the updated set of tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags updateTags(GroupId id, TagsUpdate tags)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return updateTagsAsync(id, tags, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Updates the tags of the group with the given identifier.
	 * 
	 * @param id
	 *            identifier of the group
	 * @param tags
	 *            the tag update object
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the updated set of tags
	 */
	public Future<Tags> updateTagsAsync(GroupId id, TagsUpdate tags,
	        FutureCallback<Tags> callback) {
		HttpPost req = post(groupTagsEndpoint(id), tags);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Replaces the tags of the group with the given identifier.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #replaceTagsAsync(GroupId, Tags, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group
	 * @param tags
	 *            the replacements tags
	 * @return the new set of tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags replaceTags(GroupId id, Tags tags)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return replaceTagsAsync(id, tags, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Replaces the tags of the group with the given identifier.
	 * 
	 * @param id
	 *            identifier of the group
	 * @param tags
	 *            the replacement tags
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the new set of tags
	 */
	public Future<Tags> replaceTagsAsync(GroupId id, Tags tags,
	        FutureCallback<Tags> callback) {
		HttpPut req = put(groupTagsEndpoint(id), tags);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches the tags of the group with the given identifier.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchTagsAsync(GroupId, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the group
	 * @return the batch tags
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public Tags fetchTags(GroupId id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchTagsAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches the tags of the group with the given identifier.
	 * 
	 * @param id
	 *            identifier of the group
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the new set of tags
	 */
	public Future<Tags> fetchTagsAsync(GroupId id,
	        FutureCallback<Tags> callback) {
		HttpGet req = get(groupTagsEndpoint(id));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Tags> consumer =
		        jsonAsyncConsumer(Tags.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Creates a page fetcher to retrieve a paged list of inbound messages.
	 * Note, this method does not itself cause any network activity.
	 * 
	 * @param filter
	 *            the inbounds filter
	 * @return a future page
	 */
	public PagedFetcher<MoSms> fetchInbounds(
	        final InboundsFilter filter) {
		return new PagedFetcher<MoSms>() {

			@Override
			Future<Page<MoSms>> fetchAsync(int page,
			        FutureCallback<Page<MoSms>> callback) {
				return fetchInbounds(page, filter,
				        callbackWrapper().wrap(callback));
			}

		};
	}

	/**
	 * Fetches the given page in a paged list of inbound messages.
	 * 
	 * @param page
	 *            the page to fetch
	 * @param filter
	 *            the inbounds filter
	 * @param callback
	 *            the callback to invoke when call is finished
	 * @return a future page
	 */
	private Future<Page<MoSms>> fetchInbounds(int page,
	        InboundsFilter filter,
	        FutureCallback<Page<MoSms>> callback) {
		List<NameValuePair> params = filter.toQueryParams(page);
		HttpGet req = get(inboundsEndpoint(params));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Page<MoSms>> consumer =
		        jsonAsyncConsumer(PagedInboundsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches the inbound message having the given identifier.
	 * <p>
	 * This method blocks until the request completes and its use is
	 * discouraged. Please consider using the asynchronous method
	 * {@link #fetchInboundAsync(String, FutureCallback)} instead.
	 * 
	 * @param id
	 *            identifier of the inbound message
	 * @return the fetched message
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ErrorResponseException
	 *             if the server response indicated an error
	 * @throws ConcurrentException
	 *             if the send threw an unknown exception
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 */
	public MoSms fetchInbound(String id)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchInboundAsync(id, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Fetches the inbound message having the given identifier.
	 * 
	 * @param id
	 *            identifier of the inbound message
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the fetched message
	 */
	public Future<MoSms> fetchInboundAsync(String id,
	        FutureCallback<MoSms> callback) {
		HttpGet req = get(inboundEndpoint(id));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<MoSms> consumer =
		        jsonAsyncConsumer(MoSms.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

}
