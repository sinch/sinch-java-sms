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
 * The HTTP client used if the user did not provide a custom one. It is a direct
 * wrapper around a "proper" HTTP client and mainly functions as a marker for
 * {@link ApiConnection}.
 */
final class ApiDefaultHttpAsyncClient implements HttpAsyncClient, Closeable {

	/**
	 * The default maximum number of simultaneous connections to open towards
	 * the XMS endpoint.
	 */
	private static final int DEFAULT_MAX_CONN = 10;

	/**
	 * The underlying actual HTTP client.
	 */
	private final CloseableHttpAsyncClient client;

	public ApiDefaultHttpAsyncClient() {
		// TODO: Is this a good default setup?
		client = HttpAsyncClients.custom()
		        .disableCookieManagement()
		        .setMaxConnPerRoute(DEFAULT_MAX_CONN)
		        .setMaxConnTotal(DEFAULT_MAX_CONN)
		        .build();
	}

	void start() {
		client.start();
	}

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
