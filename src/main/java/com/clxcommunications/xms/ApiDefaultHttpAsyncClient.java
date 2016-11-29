package com.clxcommunications.xms;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

/**
 * The default HTTP client used in API connections. It is configured in a way
 * suitable for communicating with XMS and is therefore most suited for
 * communicating with a single HTTP host.
 */
public final class ApiDefaultHttpAsyncClient
        implements HttpAsyncClient, Closeable {

	/**
	 * The default maximum number of simultaneous connections to open towards
	 * the XMS endpoint.
	 */
	private static final int DEFAULT_MAX_CONN = 10;

	/**
	 * Whether this client was started internally by {@link ApiConnection}.
	 */
	private boolean startedInternally;

	/**
	 * The underlying actual HTTP client.
	 */
	private final CloseableHttpAsyncClient client;

	/**
	 * Creates a new HTTP async client suitable for communicating with XMS.
	 */
	public ApiDefaultHttpAsyncClient() {
		this(false);
	}

	/**
	 * Creates a new HTTP async client suitable for communicating with XMS.
	 * 
	 * @param startedInternally
	 *            whether this object was created inside this SDK
	 */
	ApiDefaultHttpAsyncClient(boolean startedInternally) {
		this.startedInternally = startedInternally;

		// TODO: Is this a good default setup?
		this.client =
		        HttpAsyncClients.custom()
		                .disableCookieManagement()
		                .setMaxConnPerRoute(DEFAULT_MAX_CONN)
		                .setMaxConnTotal(DEFAULT_MAX_CONN)
		                .build();
	}

	/**
	 * Whether this object was created inside the SDK.
	 * 
	 * @return <code>true</code> if internally generated, <code>false</code>
	 *         otherwise
	 */
	boolean isStartedInternally() {
		return startedInternally;
	}

	/**
	 * Whether this client is started.
	 * 
	 * @return <code>true</code> if started, <code>false</code> otherwise
	 */
	public boolean isRunning() {
		return client.isRunning();
	}

	/**
	 * Starts this client.
	 */
	public void start() {
		client.start();
	}

	/**
	 * Closes this client and releases any held resources.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public void close() throws IOException {
		client.close();
	}

	@Override
	public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer,
	        HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context,
	        FutureCallback<T> callback) {
		return client.execute(requestProducer, responseConsumer, context,
		        callback);
	}

	@Override
	public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer,
	        HttpAsyncResponseConsumer<T> responseConsumer,
	        FutureCallback<T> callback) {
		return client.execute(requestProducer, responseConsumer, callback);
	}

	@Override
	public Future<HttpResponse> execute(HttpHost target, HttpRequest request,
	        HttpContext context, FutureCallback<HttpResponse> callback) {
		return client.execute(target, request, context, callback);
	}

	@Override
	public Future<HttpResponse> execute(HttpHost target, HttpRequest request,
	        FutureCallback<HttpResponse> callback) {
		return client.execute(target, request, callback);
	}

	@Override
	public Future<HttpResponse> execute(HttpUriRequest request,
	        HttpContext context, FutureCallback<HttpResponse> callback) {
		return client.execute(request, context, callback);
	}

	@Override
	public Future<HttpResponse> execute(HttpUriRequest request,
	        FutureCallback<HttpResponse> callback) {
		return client.execute(request, callback);
	}

}
