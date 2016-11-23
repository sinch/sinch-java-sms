package com.clxcommunications.xms;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.concurrent.FutureCallback;

import com.clxcommunications.xms.api.Page;
import com.fasterxml.jackson.core.JsonProcessingException;

@NotThreadSafe
public abstract class PagedFetcher<T> {

	public PagedFetcher() {
	}

	Page<T> fetch(int page) throws JsonProcessingException, ExecutionException,
	        ApiException, UnexpectedResponseException, InterruptedException {
		try {
			return fetchAsync(page, null).get();
		} catch (ExecutionException e) {
			throw Utils.maybeUnwrapExecutionException(e);
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
	 * <p>
	 * Note, since multiple fetches may be necessary to iterate over all batches
	 * it is possible that concurrent changes on the server will cause the same
	 * batch to be iterated over twice.
	 * <p>
	 * Note, since the returned iterator will perform asynchronous network
	 * traffic it is possible that the {@link Iterator#hasNext()} and
	 * {@link Iterator#next()} methods throws {@link RuntimeException} having as
	 * cause an {@link ExecutionException}.
	 * 
	 * @return a non-null iterable
	 * @throws RuntimeException
	 *             if the background page fetching failed
	 */
	public Iterable<T> elements() {

		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {

				final Iterator<Page<T>> pageIt = pages().iterator();

				return new Iterator<T>() {

					Iterator<T> pageElemIt = pageIt.next().iterator();

					@Override
					public boolean hasNext() {
						if (!pageElemIt.hasNext()) {
							if (!pageIt.hasNext()) {
								return false;
							} else {
								pageElemIt = pageIt.next().iterator();
								return pageElemIt.hasNext();
							}
						} else {
							return true;
						}
					}

					@Override
					public T next() {
						if (!pageElemIt.hasNext()) {
							pageElemIt = pageIt.next().iterator();
						}

						return pageElemIt.next();
					}

				};

			}

		};
	}

	/**
	 * Returns an iterable object that fetches and traverses all matching pages.
	 * Note, each iteration will result in a network fetch.
	 * <p>
	 * This iterator will always yield at least one page, which might be empty.
	 * <p>
	 * Note, since the returned iterator will perform asynchronous network
	 * traffic it is possible that the {@link Iterator#next()} method throws
	 * {@link RuntimeException} having as cause an {@link ExecutionException}.
	 * 
	 * @return a non-null iterable
	 * @throws RuntimeException
	 *             if the background page fetching failed
	 */
	public Iterable<Page<T>> pages() {

		return new Iterable<Page<T>>() {

			@Override
			public Iterator<Page<T>> iterator() {

				return new Iterator<Page<T>>() {

					Page<T> page = null;

					@Override
					public boolean hasNext() {
						if (page == null) {
							return true;
						} else {
							return page.page() + 1 < page.numPages();
						}
					}

					@Override
					public Page<T> next() {
						int pageToFetch =
						        (page == null) ? 0 : page.page() + 1;

						try {
							page = fetchAsync(pageToFetch, null).get();
						} catch (InterruptedException e) {
							// Interrupt the thread to let upstream code know.
							Thread.currentThread().interrupt();
						} catch (ExecutionException e) {
							throw new RuntimeException(e);
						}

						return page;
					}

				};

			}

		};
	}

}
