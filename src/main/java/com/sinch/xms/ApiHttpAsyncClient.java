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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.threeten.bp.Duration;

/**
 * An asynchronous HTTP client used in API connections. It is configured in a
 * way suitable for communicating with XMS and is therefore most applicable for
 * communicating with a single HTTP host.
 * <p>
 * It is in most cases sufficient to let {@link ApiConnection} create and manage
 * the HTTP client. If necessary, however, it is possible to create and manage
 * this type of connections manually.
 */
public class ApiHttpAsyncClient implements HttpAsyncClient, Closeable {

	/**
	 * The default limit for the socket and connect timeout.
	 */
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

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
	 * Creates a new HTTP asynchronous client suitable for communicating with
	 * XMS.
	 * 
	 * @param startedInternally
	 *            whether this object was created inside this SDK
	 */
	ApiHttpAsyncClient(boolean startedInternally) {
		this.startedInternally = startedInternally;

		// Allow TLSv1.2 protocol only
		SSLIOSessionStrategy sslSessionStrategy =
		        new SSLIOSessionStrategy(
		                SSLContexts.createSystemDefault(),
		                new String[] { "TLSv1.2" },
		                null,
		                SSLIOSessionStrategy.getDefaultHostnameVerifier());

		RequestConfig requestConfig =
		        RequestConfig.custom()
		                .setConnectTimeout((int) DEFAULT_TIMEOUT.toMillis())
		                .setSocketTimeout((int) DEFAULT_TIMEOUT.toMillis())
		                .build();

		// TODO: Is this a good default setup?
		this.client =
		        HttpAsyncClients.custom()
		                .setSSLStrategy(sslSessionStrategy)
		                .disableCookieManagement()
		                .setMaxConnPerRoute(DEFAULT_MAX_CONN)
		                .setMaxConnTotal(DEFAULT_MAX_CONN)
		                .setDefaultRequestConfig(requestConfig)
		                .build();
	}

	/**
	 * Creates a new asynchronous HTTP client suitable for communicating with
	 * XMS.
	 * 
	 * @return a newly constructed HTTP client
	 */
	@Nonnull
	public static ApiHttpAsyncClient of() {
		return new ApiHttpAsyncClient(false);
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
