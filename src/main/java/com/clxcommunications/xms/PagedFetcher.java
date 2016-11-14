package com.clxcommunications.xms;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.concurrent.FutureCallback;

import com.fasterxml.jackson.core.JsonParseException;

@NotThreadSafe
public abstract class PagedFetcher<T> {

	public PagedFetcher() {
	}

	Page<T> fetch(int page)
	        throws InterruptedException, ExecutionException, ApiException,
	        JsonParseException {
		try {
			return fetchAsync(page, null).get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof ApiException) {
				throw (ApiException) e.getCause();
			} else if (e.getCause() instanceof JsonParseException) {
				throw (JsonParseException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/**
	 * Attempts to asynchronously fetch the given page.
	 * 
	 * @param page
	 *            page to fetch (staring from zero)
	 * @param callback
	 *            request callback
	 * @return a future providing the requested page
	 */
	abstract Future<Page<T>> fetchAsync(int page,
	        FutureCallback<Page<T>> callback);

	/**
	 * Fetch all pages, some or all pages may be fetched asynchronously.
	 * 
	 * @param callback
	 *            request callback, called once per page
	 * @return a queue of futures, one for each fetched page
	 * @throws URISyntaxException
	 */
	Queue<Future<Page<T>>> fetchPagesAsync(
	        final FutureCallback<Page<T>> callback) {
		final Queue<Future<Page<T>>> futures =
		        new ConcurrentLinkedQueue<Future<Page<T>>>();

		Future<Page<T>> initialFuture =
		        fetchAsync(0, new FutureCallback<Page<T>>() {

			        @Override
			        public void completed(Page<T> result) {
				        callback.completed(result);

				        /*
				         * Go over all remaining pages and fetch them. Note,
				         * they are all submitted to the HTTP client library
				         * asynchronously!
				         */
				        for (int i = 1; i < result.numPages(); i++) {
					        futures.add(fetchAsync(i, callback));
				        }
			        }

			        @Override
			        public void failed(Exception ex) {
				        callback.failed(ex);
			        }

			        @Override
			        public void cancelled() {
				        callback.cancelled();
			        }

		        });

		futures.add(initialFuture);

		return futures;
	}

	/**
	 * Returns an iterable object that traverses all fetched elements across all
	 * associated pages. Note, internally this is done by iterating over fetched
	 * pages and, when necessary, fetching new pages.
	 * 
	 * @return a non-null iterable
	 */
	public Iterable<T> elements() {
		// TODO Implement
		return Collections.emptyList();
	}

	/**
	 * Returns an iterable object that fetches and traverses all matching pages.
	 * Note, each iteration will result in a network fetch.
	 * 
	 * @return a non-null iterable
	 */
	public Iterable<Page<T>> pages() {
		// TODO Implement
		return Collections.emptyList();
	}

}
