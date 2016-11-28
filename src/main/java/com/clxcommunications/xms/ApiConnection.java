package com.clxcommunications.xms;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clxcommunications.xms.api.BatchDeliveryReport;
import com.clxcommunications.xms.api.BatchId;
import com.clxcommunications.xms.api.MtBatchBinarySmsCreate;
import com.clxcommunications.xms.api.MtBatchBinarySmsResult;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsResult;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.Page;
import com.clxcommunications.xms.api.PagedBatchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * An abstract representation of an XMS connection. This class exposes a number
 * of methods which can be called to interact with the XMS REST API.
 * <p>
 * To instantiate this class it is necessary to use a builder, see
 * {@link #builder()}. The builder can be used to configure the connection as
 * necessary, once the connection is opened with {@link Builder#start()} it is
 * necessary to later close the connection using {@link #close()}.
 */
@Value.Immutable(copy = false)
@ValueStylePackageDirect
public abstract class ApiConnection implements Closeable {

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

		@Override
		public ApiConnection build() {
			ApiConnection conn = super.build();

			conn.json.configure(SerializationFeature.INDENT_OUTPUT,
			        conn.prettyPrintJson());

			return conn;
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

		if (httpClient() instanceof ApiDefaultHttpAsyncClient) {
			((ApiDefaultHttpAsyncClient) httpClient()).close();
		} else {
			log.debug("Not closing HTTP client since it was given externally");
		}
	}

	/**
	 * The XMS authorization token.
	 * 
	 * @return a non-null string
	 */
	public abstract String token();

	/**
	 * The XMS service username.
	 * 
	 * @return a non-null string
	 */
	public abstract String username();

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
		return new ApiDefaultHttpAsyncClient();
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
		return URI.create("https://api.mblox.com/xms/v1");
	}

	/**
	 * The HTTP host providing the XMS API.
	 * 
	 * @return a non-null host specification
	 */
	@Value.Derived
	public HttpHost endpointHost() {
		URI uri = endpoint();
		return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
	}

	/**
	 * The endpoint base path. The default value is
	 * <code>/xms/v1/{username}</code>.
	 * 
	 * @return the endpoint base path
	 */
	@Value.Default
	public String endpointBasePath() {
		return endpoint().getPath();
	}

	/**
	 * Validates that this object is in a coherent state.
	 */
	@Value.Check
	protected void check() {
		if (endpoint().getQuery() != null) {
			throw new IllegalStateException(
			        "base endpoint has query component");
		}

		if (endpoint().getFragment() != null) {
			throw new IllegalStateException(
			        "base endpoint has fragment component");
		}
	}

	/**
	 * Helper returning an endpoint URL for the given sub-path and query.
	 * 
	 * @param subPath
	 *            path fragment to place after the base path
	 * @param query
	 *            the query string, may be null
	 * @return a non-null endpoint URL
	 */
	@Nonnull
	private URI endpoint(@Nonnull String subPath, @Nullable String query) {
		StringBuilder sb = new StringBuilder();

		sb.append(endpointHost().toURI());
		sb.append('/').append(username());
		sb.append(endpointBasePath());
		sb.append(subPath);
		if (query != null) {
			sb.append('?').append(query);
		}

		return URI.create(sb.toString());
	}

	/**
	 * Like {@link #endpoint(String, String)} but with an empty query string.
	 * 
	 * @param subPath
	 *            path fragment to place after the base path
	 * @return a non-null endpoint URL
	 * @throws IllegalStateException
	 *             if the generated URL is invalid, wraps the
	 *             {@link URISyntaxException}
	 */
	@Nonnull
	private URI endpoint(String subPath) {
		return endpoint(subPath, null);
	}

	@Nonnull
	private URI batchesEndpoint() {
		return endpoint("/batches", null);
	}

	@Nonnull
	private URI batchEndpoint(BatchId batchId) {
		return endpoint("/batches/" + batchId.id());
	}

	@Nonnull
	private URI batchDeliveryReportEndpoint(BatchId batchId, String query) {
		return endpoint(
		        "/batches/" + batchId.id() + "/delivery_report",
		        query);
	}

	@Nonnull
	private URI batchDryRunEndpoint() {
		return endpoint("/batches/dry_run");
	}

	@Nonnull
	private URI batchRecipientDeliveryReportEndpoint(BatchId batchId,
	        String recipient) {
		return endpoint(
		        "/batches/" + batchId.id() + "/delivery_report/" + recipient);
	}

	/**
	 * Helper that produces a HTTP consumer that consumes the given class as a
	 * JSON object.
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
		req.setHeader("X-CLX-SdkVersion", Version.VERSION);
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
	 * Attempts to send the given batch synchronously. Internally this uses an
	 * asynchronous call and blocks until it completes.
	 * 
	 * @param sms
	 *            the batch to send
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
	public MtBatchTextSmsResult sendBatch(MtBatchTextSmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return sendBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Attempts to send the given batch synchronously. Internally this uses an
	 * asynchronous call and blocks until it completes.
	 * 
	 * @param sms
	 *            the batch to send
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
	public MtBatchBinarySmsResult sendBatch(MtBatchBinarySmsCreate sms)
	        throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return sendBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously submits the given text batch.
	 * 
	 * @param sms
	 *            the batch to send
	 * @param callback
	 *            a callback that is invoked when submit is completed
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchTextSmsResult> sendBatchAsync(MtBatchTextSmsCreate sms,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpPost httpPost = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Asynchronously submits the given binary batch.
	 * 
	 * @param sms
	 *            the batch to send
	 * @param callback
	 *            a callback that is invoked when submit is completed
	 * @return a future whose result is the creation response
	 */
	public Future<MtBatchBinarySmsResult> sendBatchAsync(
	        MtBatchBinarySmsCreate sms,
	        FutureCallback<MtBatchBinarySmsResult> callback) {
		HttpPost httpPost = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Attempts to replace the given batch synchronously. Internally this uses
	 * an asynchronous call and blocks until it completes.
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
	 * Attempts to replace the given batch synchronously. Internally this uses
	 * an asynchronous call and blocks until it completes.
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
	 * Asynchronously replaces the given text batch.
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
		HttpPut httpPut = put(batchEndpoint(id), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPut);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Asynchronously replaces the given binary batch.
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
		HttpPut httpPut = put(batchEndpoint(id), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPut);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> responseConsumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Updates the given text batch. The update is performed synchronously.
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
	 * Updates the given binary batch. The update is performed synchronously.
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
		HttpPost httpPost = post(batchEndpoint(batchId), sms);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> consumer =
		        jsonAsyncConsumer(MtBatchTextSmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
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
		HttpPost httpPost = post(batchEndpoint(batchId), sms);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> consumer =
		        jsonAsyncConsumer(MtBatchBinarySmsResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches the given batch. Blocks until the fetch has completed.
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
		String query = filter.toUrlEncodedQuery(page);
		URI url = endpoint("/batches", query);

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
	 * Fetches a delivery report for the batch with the given batch ID. Blocks
	 * until the fetch has completed.
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
		String query = filter.toUrlEncodedQuery();
		HttpGet req = get(batchDeliveryReportEndpoint(id, query));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<BatchDeliveryReport> consumer =
		        jsonAsyncConsumer(BatchDeliveryReport.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

}
