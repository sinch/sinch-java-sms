package com.clxcommunications.xms;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.clxcommunications.xms.api.ApiError;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Value.Immutable(copy = false)
@Value.Style(depluralize = true, jdkOnly = true, from = "using",
        build = "start", visibility = ImplementationVisibility.PACKAGE)
public abstract class ApiConnection implements Closeable {

	public static class Builder extends ImmutableApiConnection.Builder {

		public Builder endpointHost(String hostname, int port, String scheme) {
			this.endpointHost(new HttpHost(hostname, port, scheme));
			return this;
		}

		@Override
		public ImmutableApiConnection start() {
			ImmutableApiConnection conn = super.start();

			conn.httpClient().start();

			return conn;
		}

	}

	private abstract class JsonApiAsyncConsumer<T>
	        extends AsyncCharConsumer<T> {

		private HttpResponse response;
		private StringBuilder sb;

		@Override
		protected void onCharReceived(CharBuffer buf,
		        IOControl ioctrl) throws IOException {
			sb.append(buf.toString());
		}

		@Override
		protected void onResponseReceived(HttpResponse response)
		        throws HttpException, IOException {
			this.response = response;
			this.sb = new StringBuilder();
		}

		@Nonnull
		protected abstract T buildSuccessResult(String str,
		        HttpContext context)
		        throws JsonParseException, JsonMappingException, IOException;

		@Override
		protected T buildResult(HttpContext context) throws Exception {
			int code = response.getStatusLine().getStatusCode();

			switch (code) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_CREATED:
				return buildSuccessResult(sb.toString(), context);
			case HttpStatus.SC_BAD_REQUEST:
			case HttpStatus.SC_FORBIDDEN:
				ApiError error = json.readValue(sb.toString(), ApiError.class);
				throw new ApiException(error);
			default:
				// TODO: Good idea to buffer the response in this case?
				ContentType contentType =
				        ContentType.getLenient(response.getEntity());
				response.setEntity(
				        new StringEntity(sb.toString(), contentType));
				throw new UnexpectedResponseException(response);
			}
		}

	}

	private final class BatchSmsResultAsyncConsumer
	        extends JsonApiAsyncConsumer<MtBatchSmsResult> {

		@Override
		protected MtBatchSmsResult buildSuccessResult(String str,
		        HttpContext context) throws JsonParseException,
		        JsonMappingException, IOException {
			return json.readValue(str, MtBatchSmsResult.class);
		}

	}

	private final class BatchTextSmsResultAsyncConsumer
	        extends JsonApiAsyncConsumer<MtBatchTextSmsResult> {

		@Override
		protected MtBatchTextSmsResult buildSuccessResult(String str,
		        HttpContext context) throws JsonParseException,
		        JsonMappingException, IOException {
			return json.readValue(str, MtBatchTextSmsResult.class);
		}

	}

	private final class BatchBinarySmsResultAsyncConsumer
	        extends JsonApiAsyncConsumer<MtBatchBinarySmsResult> {

		@Override
		protected MtBatchBinarySmsResult buildSuccessResult(String str,
		        HttpContext context) throws JsonParseException,
		        JsonMappingException, IOException {
			return json.readValue(str, MtBatchBinarySmsResult.class);
		}

	}

	private final class PagedResultAsyncConsumer<P extends Page<T>, T>
	        extends JsonApiAsyncConsumer<Page<T>> {

		final private Class<P> clazz;

		private PagedResultAsyncConsumer(Class<P> clazz) {
			this.clazz = clazz;
		}

		@Override
		protected Page<T> buildSuccessResult(String str, HttpContext context)
		        throws JsonParseException, JsonMappingException, IOException {
			return json.readValue(str, clazz);
		}

	}

	/**
	 * A Jackson object mapper.
	 */
	private final ApiObjectMapper json;

	/**
	 * Package visibility because one implementation should be enough.
	 */
	ApiConnection() {
		json = new ApiObjectMapper();
	}

	@Nonnull
	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void close() throws IOException {
		httpClient().close();
	}

	/**
	 * Authorization token.
	 * 
	 * @return a non-null string
	 */
	public abstract String token();

	/**
	 * Service username.
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
	 * Note, this HTTP client is closed when this API connection is closed.
	 * 
	 * @return a non-null HTTP client
	 */
	@Value.Default
	public CloseableHttpAsyncClient httpClient() {
		return HttpAsyncClients.createMinimal();
	}

	/**
	 * The future callback wrapper to use in all API calls. By default this is
	 * {@link CallbackWrapper.ExceptionDropper}, that is, any exception thrown
	 * in a given callback is logged and dropped.
	 * 
	 * @return a non-null callback wrapper
	 */
	@Value.Default
	public CallbackWrapper callbackWrapper() {
		return CallbackWrapper.exceptionDropper;
	}

	public abstract HttpHost endpointHost();

	/**
	 * The endpoint base path. The default value is
	 * <code>/xms/v1/{username}</code>.
	 * 
	 * @return the endpoint base path
	 */
	@Value.Default
	public String endpointBasePath() {
		try {
			return "/xms/v1/" + URLEncoder.encode(username(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Value.Check
	protected void check() {
		if (!endpointBasePath().startsWith("/")) {
			throw new IllegalStateException(
			        "endpoint base path does not start with '/'");
		}

		if (endpointBasePath().contains("?")) {
			throw new IllegalStateException("endpoint base path contains '?'");
		}

		/*
		 * Attempt to create a plain endpoint URL. If it fails then something is
		 * very wrong with the host or the endpoint base path. If it succeeds
		 * then all endpoints generated in normal use of this class should
		 * succeed since we validate the user input.
		 * 
		 * Note, this does not mean that the generated URL makes sense, it only
		 * means that the code will not throw exceptions. For example, if the
		 * user sets the endpoint base path, "/hello?world" then all bets are
		 * off.
		 */
		endpoint("", null);
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
	private URI batchDeliveryReportEndpoint(BatchId batchId, String type) {
		return endpoint(
		        "/batches/" + batchId.id() + "/delivery_report",
		        "type=" + type);
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
	 * Posts a JSON serialization of the given object to the given endpoint.
	 * 
	 * @param endpoint
	 *            the target endpoint
	 * @param object
	 *            the object whose JSON representation is sent
	 * @return a HTTP post request.
	 */
	private <T> HttpPost post(URI endpoint, T object) {
		final byte[] content;

		/*
		 * Attempt to serialize the given object into JSON. Note, we swallow the
		 * JsonProcessingException since we control which objects will be
		 * serialized and can guarantee that they all should be serializable.
		 * Thus, if the exception still is thrown it indicates a severe bug in
		 * internal state management.
		 */
		try {
			content = json.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}

		ByteArrayEntity entity =
		        new ByteArrayEntity(content, ContentType.APPLICATION_JSON);

		HttpPost req = new HttpPost(endpoint);

		req.setHeader("Authorization", "Bearer " + token());
		req.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
		req.setEntity(entity);

		return req;
	}

	private HttpGet get(URI endpoint) {
		HttpGet req = new HttpGet(endpoint);
		req.setHeader("Authorization", "Bearer " + token());
		req.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
		return req;
	}

	private HttpDelete delete(URI endpoint) {
		HttpDelete req = new HttpDelete(endpoint);
		req.setHeader("Authorization", "Bearer " + token());
		req.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
		return req;
	}

	/**
	 * Unwrap exceptions for synchronous send methods. This helper will examine
	 * an {@link ExecutionException} object and unwrap and throw it's cause if
	 * it makes sense for a synchronous call.
	 * 
	 * @param e
	 *            the exception to examine
	 * @return returns <code>e</code>
	 * @throws ApiException
	 * @throws UnexpectedResponseException
	 * @throws JsonProcessingException
	 * @throws ExecutionException
	 */
	private ExecutionException maybeUnwrapExecutionException(
	        ExecutionException e)
	        throws ApiException, UnexpectedResponseException,
	        JsonProcessingException, ExecutionException {
		if (e.getCause() instanceof ApiException) {
			throw (ApiException) e.getCause();
		} else if (e.getCause() instanceof UnexpectedResponseException) {
			throw (UnexpectedResponseException) e.getCause();
		} else if (e.getCause() instanceof JsonProcessingException) {
			throw (JsonProcessingException) e.getCause();
		}

		return e;
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
	 * @throws ExecutionException
	 *             if the send throw an exception other than
	 *             {@link ApiException}
	 * @throws ApiException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 * @throws JsonProcessingException
	 *             if JSON deserialization failed
	 */
	public MtBatchTextSmsResult sendBatch(MtBatchTextSmsCreate sms)
	        throws InterruptedException, ExecutionException, ApiException,
	        JsonProcessingException, UnexpectedResponseException {
		try {
			return sendBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw maybeUnwrapExecutionException(e);
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
	 * @throws ExecutionException
	 *             if the send threw an unknown exception
	 * @throws ApiException
	 *             if the server response indicated an error
	 * @throws UnexpectedResponseException
	 *             if the server gave an unexpected response
	 * @throws JsonProcessingException
	 *             if JSON deserialization failed
	 */
	public MtBatchBinarySmsResult sendBatch(MtBatchBinarySmsCreate sms)
	        throws InterruptedException, ExecutionException, ApiException,
	        JsonProcessingException, UnexpectedResponseException {
		try {
			return sendBatchAsync(sms, null).get();
		} catch (ExecutionException e) {
			throw maybeUnwrapExecutionException(e);
		}
	}

	public Future<MtBatchTextSmsResult> sendBatchAsync(MtBatchTextSmsCreate sms,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpPost httpPost = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchTextSmsResult> responseConsumer =
		        new BatchTextSmsResultAsyncConsumer();

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
	}

	public Future<MtBatchBinarySmsResult> sendBatchAsync(
	        MtBatchBinarySmsCreate sms,
	        FutureCallback<MtBatchBinarySmsResult> callback) {
		HttpPost httpPost = post(batchesEndpoint(), sms);

		HttpAsyncRequestProducer requestProducer =
		        new BasicAsyncRequestProducer(endpointHost(), httpPost);
		HttpAsyncResponseConsumer<MtBatchBinarySmsResult> responseConsumer =
		        new BatchBinarySmsResultAsyncConsumer();

		return httpClient().execute(requestProducer, responseConsumer,
		        callbackWrapper().wrap(callback));
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
		        new BatchTextSmsResultAsyncConsumer();

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
		        new BatchBinarySmsResultAsyncConsumer();

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	/**
	 * Fetches a batch with the given batch ID.
	 * 
	 * @param batchId
	 *            ID of the batch to fetch
	 * @param callback
	 *            a callback that is activated at call completion
	 * @return a future yielding the updated status of the batch
	 */
	public Future<MtBatchSmsResult> fetchBatch(BatchId batchId,
	        FutureCallback<MtBatchSmsResult> callback) {
		HttpGet req = get(batchEndpoint(batchId));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<MtBatchSmsResult> consumer =
		        new BatchSmsResultAsyncConsumer();

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	public PagedFetcher<MtBatchSmsResult> fetchBatches(final BatchFilter filter,
	        final FutureCallback<Page<MtBatchSmsResult>> callback) {
		return new PagedFetcher<MtBatchSmsResult>() {

			@Override
			Future<Page<MtBatchSmsResult>> fetchAsync(int page,
			        FutureCallback<Page<MtBatchSmsResult>> callback) {
				return fetchBatches(page, filter,
				        callbackWrapper().wrap(callback));
			}

		};
	}

	private Future<Page<MtBatchSmsResult>> fetchBatches(int page,
	        BatchFilter filter,
	        FutureCallback<Page<MtBatchSmsResult>> callback) {
		String query = filter.toUrlEncodedQuery(page);
		URI url = endpoint("/batches", query);

		HttpGet req = get(url);

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<Page<MtBatchSmsResult>> consumer =
		        new PagedResultAsyncConsumer<PagedBatchResult, MtBatchSmsResult>(
		                PagedBatchResult.class);

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

	public Future<MtBatchTextSmsResult> cancelBatch(BatchId batchId) {
		return cancelBatch(batchId, null);
	}

	public Future<MtBatchTextSmsResult> cancelBatch(BatchId batchId,
	        FutureCallback<MtBatchTextSmsResult> callback) {
		HttpDelete req = delete(batchEndpoint(batchId));

		HttpAsyncRequestProducer producer =
		        new BasicAsyncRequestProducer(endpointHost(), req);

		HttpAsyncResponseConsumer<MtBatchTextSmsResult> consumer =
		        new BatchTextSmsResultAsyncConsumer();

		return httpClient().execute(producer, consumer,
		        callbackWrapper().wrap(callback));
	}

}
