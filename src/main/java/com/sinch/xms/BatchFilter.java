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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

/** Describes a filter for limiting results when fetching batches. */
@Value.Immutable
@ValueStylePackage
public abstract class BatchFilter {

  /** A builder of batch filters. */
  public static class Builder extends BatchFilterImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link BatchFilter} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final BatchFilter.Builder builder() {
    return new Builder();
  }

  /**
   * The requested number of entries per page. A non-positive value means that the default value
   * will be used.
   *
   * @return the desired page size
   */
  @Value.Default
  public int pageSize() {
    return 0;
  }

  /**
   * Limits results to batches sent at or after this date.
   *
   * @return a date
   */
  @Nullable
  public abstract OffsetDateTime startDate();

  /**
   * Limits results to batches send before this date.
   *
   * @return a date
   */
  @Nullable
  public abstract OffsetDateTime endDate();

  /**
   * Limits results to batches sent from the given addresses. If empty then all origins are
   * included.
   *
   * @return a non-null set of origins
   */
  public abstract Set<String> senders();

  /**
   * Limits results to batches having <em>any</em> the given tags.
   *
   * @return a non-null set of tags
   */
  public abstract Set<String> tags();

  /**
   * Limits results to batches sent with the specified client reference. If empty then all client
   * references are eligible.
   *
   * @return a client reference
   */
  @Nullable
  public abstract String clientReference();

  /** Verifies that the object is in a reasonable state. */
  @Value.Check
  protected void check() {
    for (String s : senders()) {
      if (s.contains(",")) {
        throw new IllegalStateException("from contains comma");
      }
    }

    for (String s : tags()) {
      if (s.contains(",")) {
        throw new IllegalStateException("tags contains comma");
      }
    }
  }

  /**
   * Formats this filter as an URL encoded list of query parameters.
   *
   * @param page the page to request
   * @return a list of query parameters
   */
  @Nonnull
  List<NameValuePair> toQueryParams(int page) {
    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(6);

    params.add(new BasicNameValuePair("page", String.valueOf(page)));

    if (pageSize() > 0) {
      params.add(new BasicNameValuePair("page_size", String.valueOf(pageSize())));
    }

    if (startDate() != null) {
      params.add(new BasicNameValuePair("start_date", startDate().toString()));
    }

    if (endDate() != null) {
      params.add(new BasicNameValuePair("end_date", endDate().toString()));
    }

    if (!senders().isEmpty()) {
      params.add(new BasicNameValuePair("from", Utils.join(",", senders())));
    }

    if (!tags().isEmpty()) {
      params.add(new BasicNameValuePair("tags", Utils.join(",", tags())));
    }

    if (clientReference() != null) {
      params.add(new BasicNameValuePair("client_reference", clientReference()));
    }

    return params;
  }
}
