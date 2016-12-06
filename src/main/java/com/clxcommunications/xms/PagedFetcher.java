/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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
package com.clxcommunications.xms;

import java.util.Iterator;
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
	 *            page to fetch (starting from zero)
	 * @param callback
	 *            request callback
	 * @return a future providing the requested page
	 */
	@Nonnull
	abstract Future<Page<T>> fetchAsync(int page,
	        @Nullable FutureCallback<Page<T>> callback);

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

					private Page<T> page = null;
					private int seenElements = 0;

					@Override
					public boolean hasNext() {
						if (page == null) {
							return true;
						} else {
							return seenElements < page.totalSize()
							        && !page.isEmpty();
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

						seenElements += page.size();

						return page;
					}

				};

			}

		};
	}

}
