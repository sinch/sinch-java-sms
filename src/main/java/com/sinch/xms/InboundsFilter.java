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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

/** Describes a filter for limiting results when fetching inbound messages. */
@Value.Immutable
@ValueStylePackage
public abstract class InboundsFilter {

  /** A builder of inbound messages filter. */
  public static class Builder extends InboundsFilterImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link InboundsFilter} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final InboundsFilter.Builder builder() {
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
   * Limits results to inbound messages sent at or after this date.
   *
   * @return a date
   */
  @Nullable
  public abstract LocalDate startDate();

  /**
   * Limits results to inbound messages send before this date.
   *
   * @return a date
   */
  @Nullable
  public abstract LocalDate endDate();

  /**
   * Limits results to inbound messages destined for one of these numbers. If empty then all message
   * destinations are eligible.
   *
   * @return a set of message destinations
   */
  public abstract Set<String> recipients();

  /**
   * Limits results to inbound messages sent with the specified client reference. If empty then all
   * client references are eligible.
   *
   * @return a client reference
   */
  @Nullable
  public abstract String clientReference();

  /** Verifies that this filter is in a reasonable state. */
  @Value.Check
  protected void check() {
    for (String s : recipients()) {
      if (s.contains(",")) {
        throw new IllegalStateException("to contains comma");
      }
    }
  }

  /**
   * Formats this filter as an URL encoded list of query parameters.
   *
   * @param page the page to request
   * @return a non-null string containing query parameters
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

    if (!recipients().isEmpty()) {
      params.add(new BasicNameValuePair("to", Utils.join(",", recipients())));
    }

    if (clientReference() != null) {
      params.add(new BasicNameValuePair("client_reference", clientReference()));
    }

    return params;
  }
}
