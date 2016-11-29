package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

public class ApiConnectionTest {

	private static abstract class DummyClient
	        implements HttpAsyncClient, Closeable {

		@Override
		public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer,
		        HttpAsyncResponseConsumer<T> responseConsumer,
		        HttpContext context, FutureCallback<T> callback) {
			throw new AssertionError("unexpected");
		}

		@Override
		public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer,
		        HttpAsyncResponseConsumer<T> responseConsumer,
		        FutureCallback<T> callback) {
			throw new AssertionError("unexpected");
		}

		@Override
		public Future<HttpResponse> execute(HttpHost target,
		        HttpRequest request, HttpContext context,
		        FutureCallback<HttpResponse> callback) {
			throw new AssertionError("unexpected");
		}

		@Override
		public Future<HttpResponse> execute(HttpHost target,
		        HttpRequest request, FutureCallback<HttpResponse> callback) {
			throw new AssertionError("unexpected");
		}

		@Override
		public Future<HttpResponse> execute(HttpUriRequest request,
		        HttpContext context, FutureCallback<HttpResponse> callback) {
			throw new AssertionError("unexpected");
		}

		@Override
		public Future<HttpResponse> execute(HttpUriRequest request,
		        FutureCallback<HttpResponse> callback) {
			throw new AssertionError("unexpected");
		}

	}

	@Test
	public void canBuildWithAllCustoms() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath")
		        .callbackWrapper(CallbackWrapper.identity)
		        .prettyPrintJson(false)
		        .httpClient(HttpAsyncClients.createMinimal())
		        .build();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsEndpointWithQueryComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath?query=yes")
		        .build();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsBasePathWithFragmentComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath#fragment")
		        .build();
	}

	@Test
	public void leavesExternalHttpClientAlone() throws Exception {
		final AtomicBoolean clientClosed = new AtomicBoolean();

		ApiConnection conn = ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .httpClient(new DummyClient() {

			        @Override
			        public void close() throws IOException {
				        clientClosed.set(true);
			        }

		        })
		        .build();

		conn.start();
		conn.close();

		assertThat(clientClosed.get(), is(false));
	}

}
