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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class InboundsFilterTest {

  @Test
  public void canGenerateMinimal() throws Exception {
    InboundsFilter filter = SinchSMSApi.inboundsFilter().build();

    List<NameValuePair> actual = filter.toQueryParams(2);

    assertThat(actual, containsInAnyOrder((NameValuePair) new BasicNameValuePair("page", "2")));
  }

  @Test
  public void canGenerateQueryParameters() throws Exception {
    InboundsFilter filter =
        SinchSMSApi.inboundsFilter()
            .pageSize(20)
            .addRecipient("12345", "9876")
            .startDate(LocalDate.of(2010, 11, 12))
            .endDate(LocalDate.of(2011, 11, 12))
            .clientReference("myReference")
            .build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(
        actual,
        containsInAnyOrder(
            (NameValuePair) new BasicNameValuePair("page", "4"),
            new BasicNameValuePair("to", "12345,9876"),
            new BasicNameValuePair("page_size", "20"),
            new BasicNameValuePair("start_date", "2010-11-12"),
            new BasicNameValuePair("end_date", "2011-11-12"),
            new BasicNameValuePair("client_reference", "myReference")));
  }

  @Test(expected = IllegalStateException.class)
  public void rejectsToWithComma() throws Exception {
    SinchSMSApi.inboundsFilter().addRecipient("123,456").build();
  }

  @Property
  public void generatesValidQueryParameters(
      int page,
      int pageSize,
      Set<String> to,
      LocalDate startDate,
      LocalDate endDate,
      String clientReference)
      throws Exception {
    // Constrain `to` to strings not containing ','
    assumeThat(to, not(hasItem(containsString(","))));

    InboundsFilter filter =
        SinchSMSApi.inboundsFilter()
            .pageSize(pageSize)
            .recipients(to)
            .startDate(startDate)
            .endDate(endDate)
            .clientReference(clientReference)
            .build();

    List<NameValuePair> params = filter.toQueryParams(page);

    // Will throw IllegalArgumentException if an invalid URI is attempted.
    new URIBuilder().addParameters(params).build();
  }
}
