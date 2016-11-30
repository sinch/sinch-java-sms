package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.concurrent.FutureCallback;

import com.clxcommunications.xms.api.Page;

/**
 * Used for API calls that emits their result over multiple pages. This class
 * includes convenience methods for interacting with such pages in various ways.
 * For example, it is possible to retrieve individual pages or to produce an
 * iterator that seamlessly will iterate over all elements of all pages.
 * 
 * @param <T>
 *            the element type
 */
public abstract class PagedFetcher<T> {

	/**
	 * Synchronously fetches the page having the given page number.
	 * 
	 * @param page
	 *            the page number (starting from zero)
	 * @return the fetched page
	 * @throws ConcurrentException
	 *             if another checked exception occurred during execution
	 * @throws ErrorResponseException
	 *             if the HTTP endpoint returned an error message
	 * @throws UnexpectedResponseException
	 *             if the HTTP endpoint responded with an unexpected message
	 * @throws InterruptedException
	 *             if the fetch was interrupted before completing
	 */
	@Nonnull
	Page<T> fetch(int page) throws InterruptedException, ConcurrentException,
	        ErrorResponseException, UnexpectedResponseException {
		try {
			return fetchAsync(page, null).get();
		} catch (ExecutionException e) {
			throw Utils.unwrapExecutionException(e);
		}
	}

	/**
	 * Asynchronously fetches the page having the given page number.
	 * 
	 * @param page
	 *            page to fetch (staring from zero)
	 * @param callback
	 *            request callback
	 * @return a future providing the requested page
	 */
	@Nonnull
	abstract Future<Page<T>> fetchAsync(int page,
	        @Nullable FutureCallback<Page<T>> callback);

	/**
	 * Fetch all pages, some or all pages may be fetched asynchronously.
	 * <p>
	 * The returned list is ordered by page number but no guarantee is made of
	 * the order in which the callback is called.
	 * 
	 * @param callback
	 *            request callback, called once per page
	 * @return a queue of futures, one for each fetched page
	 */
	@Nonnull
	List<Future<Page<T>>> fetchPagesAsync(
	        @Nullable final FutureCallback<Page<T>> callback) {
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

		List<Future<Page<T>>> result =
		        new ArrayList<Future<Page<T>>>(1 + futures.size());

		result.add(initialFuture);
		result.addAll(futures);

		return result;
	}

	/**
	 * Returns an iterable object that traverses all fetched elements across all
	 * associated pages. This is done by iterating over fetched pages and, when
	 * necessary, fetching new pages.
	 * <p>
	 * Since multiple fetches may be necessary to iterate over all batches it is
	 * possible that concurrent changes on the server will cause the same batch
	 * to be iterated over twice.
	 * <p>
	 * ALso, since the returned iterator will perform asynchronous network
	 * traffic it is possible that the {@link Iterator#hasNext()} and
	 * {@link Iterator#next()} methods throws {@link RuntimeException} having as
	 * cause an {@link ExecutionException}.
	 * 
	 * @return a non-null iterable
	 * @throws RuntimeException
	 *             if the background page fetching failed
	 */
	@Nonnull
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
	 * Each iteration will result in a network fetch.
	 * <p>
	 * This iterator will always yield at least one page, which might be empty.
	 * <p>
	 * Since the returned iterator will perform asynchronous network traffic it
	 * is possible that the {@link Iterator#next()} method throws
	 * {@link RuntimeException} having as cause an {@link ExecutionException}.
	 * 
	 * @return a non-null iterable
	 * @throws RuntimeApiException
	 *             if the background page fetching failed
	 */
	@Nonnull
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
						int pageToFetch = (page == null) ? 0 : page.page() + 1;

						try {
							page = fetchAsync(pageToFetch, null).get();
						} catch (InterruptedException e) {
							// Interrupt the thread to let upstream code know.
							Thread.currentThread().interrupt();
						} catch (ExecutionException e) {
							ApiException cause;

							try {
								cause = Utils.unwrapExecutionException(e);
							} catch (ErrorResponseException einner) {
								cause = einner;
							} catch (UnexpectedResponseException einner) {
								cause = einner;
							}

							throw new RuntimeApiException(cause);
						}

						return page;
					}

				};

			}

		};
	}

}
