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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.sinch.xms.api.FinalizedDeliveryStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class DeliveryReportFilterTest {

  @Test
  public void canGenerateMinimal() {
    DeliveryReportFilter filter = SinchSMSApi.deliveryReportFilter().build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(actual, containsInAnyOrder(new BasicNameValuePair("page", "4")));
  }

  @Test
  public void canGenerateQueryParameters() {
    DeliveryReportFilter filter =
        SinchSMSApi.deliveryReportFilter()
            .pageSize(20)
            .startDate(LocalDate.of(2010, 10, 11).atStartOfDay().atOffset(ZoneOffset.UTC))
            .endDate(LocalDate.of(2011, 10, 11).atStartOfDay().atOffset(ZoneOffset.UTC))
            .clientReference("myReference")
            .build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(
        actual,
        containsInAnyOrder(
            new BasicNameValuePair("page", "4"),
            new BasicNameValuePair("page_size", "20"),
            new BasicNameValuePair("start_date", "2010-10-11T00:00Z"),
            new BasicNameValuePair("end_date", "2011-10-11T00:00Z"),
            new BasicNameValuePair("client_reference", "myReference")));
  }

  @Property
  public void generatesValidQueryParameters(
      int page,
      int pageSize,
      Set<FinalizedDeliveryStatus> statuses,
      Set<Integer> codes,
      OffsetDateTime startDate,
      OffsetDateTime endDate,
      String clientReference)
      throws Exception {

    DeliveryReportFilter.Builder builder =
        SinchSMSApi.deliveryReportFilter()
            .statuses(statuses)
            .codes(codes)
            .startDate(startDate)
            .endDate(endDate)
            .clientReference(clientReference);

    DeliveryReportFilter filter = (codes.isEmpty()) ? builder.build() : builder.pageSize(pageSize).build();

    List<NameValuePair> params = filter.toQueryParams(page);

    // Will throw IllegalArgumentException if an invalid URI is attempted.
    new URIBuilder().addParameters(params).build();
  }
}
