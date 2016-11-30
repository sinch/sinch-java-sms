package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
import org.junit.Test;

import com.clxcommunications.testsupport.TestUtils;

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
		String token = TestUtils.freshToken();
		String spid = TestUtils.freshServicePlanId();
		URI url = URI.create("https://localhost:3000/basepath");
		CloseableHttpAsyncClient client = HttpAsyncClients.createMinimal();

		ApiConnection conn = ApiConnection.builder()
		        .token(token)
		        .servicePlanId(spid)
		        .endpoint(url)
		        .callbackWrapper(CallbackWrapper.identity)
		        .prettyPrintJson(false)
		        .httpClient(client)
		        .build();

		assertThat(conn.token(), is(token));
		assertThat(conn.servicePlanId(), is(spid));
		assertThat(conn.endpoint(), is(url));
		assertThat(conn.callbackWrapper(),
		        is(sameInstance(CallbackWrapper.identity)));
		assertThat(conn.prettyPrintJson(), is(false));
		assertThat(conn.httpClient(),
		        is(sameInstance((HttpAsyncClient) client)));
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsEndpointWithQueryComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .servicePlanId("spid")
		        .endpoint("https://localhost:3000/basepath?query=yes")
		        .build();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsBasePathWithFragmentComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .servicePlanId("spid")
		        .endpoint("https://localhost:3000/basepath#fragment")
		        .build();
	}

	@Test
	public void leavesExternalHttpClientAlone() throws Exception {
		final AtomicBoolean clientClosed = new AtomicBoolean();

		ApiConnection conn = ApiConnection.builder()
		        .token("token")
		        .servicePlanId("spid")
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

	@Test
	public void leavesExternalApiDefaultHttpClientAlone() throws Exception {
		ApiDefaultHttpAsyncClient client = new ApiDefaultHttpAsyncClient();

		ApiConnection conn = ApiConnection.builder()
		        .token("token")
		        .servicePlanId("spid")
		        .httpClient(client)
		        .build();

		client.start();
		conn.start();
		conn.close();

		assertThat(client.isRunning(), is(true));
	}

}
