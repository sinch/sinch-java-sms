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
package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Iterator;
import java.util.List;

/**
 * A page within a paged XMS response.
 *
 * @param <T> the page element type
 */
public abstract class Page<T> implements Iterable<T> {

  /**
   * The page number of this page.
   *
   * @return the page number
   */
  public abstract int page();

  /**
   * The number of elements in this page.
   *
   * @return the page size
   */
  @JsonProperty("page_size")
  public abstract int size();

  /**
   * The total number of elements across all pages.
   *
   * @return the total number of elements
   */
  @JsonProperty("count")
  public abstract int totalSize();

  /**
   * The page content.
   *
   * @return a list of page elements
   */
  public abstract List<T> content();

  /** Returns an iterator that traverses the content of this page. */
  @Override
  @JsonIgnore
  public Iterator<T> iterator() {
    return content().iterator();
  }

  /**
   * Whether this page is empty, i.e., does not contain any elements.
   *
   * @return <code>true</code> if empty, <code>false</code> otherwise
   */
  @JsonIgnore
  public final boolean isEmpty() {
    return size() == 0;
  }
}
