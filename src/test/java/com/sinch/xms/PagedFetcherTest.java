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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.sinch.xms.api.Page;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class PagedFetcherTest {

  @Property
  public void canDoStandardIterationOverPages(final List<List<Integer>> pages) throws Exception {
    // We are always able to fetch at least one page from XMS.
    assumeThat(pages, hasSize(greaterThanOrEqualTo(1)));

    PagedFetcher<Integer> fetcher = mockedFetcher(pages);

    List<List<Integer>> actual = new ArrayList<List<Integer>>();
    Iterator<Page<Integer>> it = fetcher.pages().iterator();
    while (it.hasNext()) {
      Page<Integer> page = it.next();
      actual.add(new ArrayList<Integer>(page.content()));
    }

    assertThat(actual, is(pages));
  }

  @Property
  public void canDoForEachOverPages(final List<List<Integer>> pages) throws Exception {
    // We are always able to fetch at least one page from XMS.
    assumeThat(pages, hasSize(greaterThanOrEqualTo(1)));

    PagedFetcher<Integer> fetcher = mockedFetcher(pages);

    List<List<Integer>> actual = new ArrayList<List<Integer>>();
    for (Page<Integer> p : fetcher.pages()) {
      actual.add(new ArrayList<Integer>(p.content()));
    }

    assertThat(actual, is(pages));
  }

  @Property
  public void canDoForeachOverElements(final List<List<Integer>> pages) throws Exception {
    // We are always able to fetch at least one page from XMS.
    assumeThat(pages, hasSize(greaterThanOrEqualTo(1)));

    PagedFetcher<Integer> fetcher = mockedFetcher(pages);

    // Need to flatten the input for the equality test.
    List<Integer> expected = new ArrayList<Integer>();
    for (List<Integer> p : pages) {
      expected.addAll(p);
    }

    List<Integer> actual = new ArrayList<Integer>();
    for (int a : fetcher.elements()) {
      actual.add(a);
    }

    assertThat(actual, is(expected));
  }

  private static PagedFetcher<Integer> mockedFetcher(final List<List<Integer>> pages) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    return new PagedFetcher<Integer>() {

      @Override
      Future<Page<Integer>> fetchAsync(final int page, FutureCallback<Page<Integer>> callback) {
        return executor.submit(mockedFetchCallable(pages, page));
      }
    };
  }

  private static Callable<Page<Integer>> mockedFetchCallable(
      final List<List<Integer>> pages, final int pageNum) {
    int sizeAcc = 0;
    for (List<Integer> page : pages) {
      sizeAcc += page.size();
    }
    final int totalSize = sizeAcc;

    return new Callable<Page<Integer>>() {

      @Override
      public Page<Integer> call() throws Exception {

        return new Page<Integer>() {

          @Override
          public int page() {
            return pageNum;
          }

          @Override
          public int size() {
            return pages.get(pageNum).size();
          }

          @Override
          public int totalSize() {
            return totalSize;
          }

          @Override
          public List<Integer> content() {
            return pages.get(pageNum);
          }
        };
      }
    };
  }
}
