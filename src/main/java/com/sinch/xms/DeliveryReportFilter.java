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

import com.sinch.xms.api.DeliveryStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

/** Describes a filter for limiting results when fetching delivery reports. */
@Value.Immutable
@ValueStylePackage
public abstract class DeliveryReportFilter {

  /** A builder of delivery report filters. */
  public static class Builder extends DeliveryReportFilterImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link DeliveryReportFilter} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final DeliveryReportFilter.Builder builder() {
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
  public abstract Set<DeliveryStatus> statuses();

  /**
   * Limits results to batches having <em>any</em> the given tags.
   *
   * @return a non-null set of tags
   */
  public abstract Set<Integer> codes();

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

    if (!statuses().isEmpty()) {
      params.add(new BasicNameValuePair("status", Utils.join(",", mapStatuses())));
    }

    if (!codes().isEmpty()) {
      params.add(new BasicNameValuePair("code", Utils.join(",", mapCodes())));
    }

    if (startDate() != null) {
      params.add(new BasicNameValuePair("start_date", startDate().toString()));
    }

    if (endDate() != null) {
      params.add(new BasicNameValuePair("end_date", endDate().toString()));
    }

    return params;
  }

  private List<String> mapCodes() {
    return codes().stream()
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .collect(Collectors.toList());
  }

  private List<String> mapStatuses() {
    return statuses().stream()
        .filter(Objects::nonNull)
        .map(DeliveryStatus::status)
        .collect(Collectors.toList());
  }
}
