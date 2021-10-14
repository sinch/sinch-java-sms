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
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;

@RunWith(JUnitQuickcheck.class)
public class BatchFilterTest {

  @Test
  public void canGenerateMinimal() throws Exception {
    BatchFilter filter = SinchSMSApi.batchFilter().build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(actual, containsInAnyOrder((NameValuePair) new BasicNameValuePair("page", "4")));
  }

  @Test
  public void canGenerateQueryParameters() throws Exception {
    BatchFilter filter =
        SinchSMSApi.batchFilter()
            .pageSize(20)
            .addSender("12345", "6789")
            .addTag("tag1", "таг2")
            .startDate(LocalDate.of(2010, 10, 11))
            .endDate(LocalDate.of(2011, 10, 11))
            .build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(
        actual,
        containsInAnyOrder(
            (NameValuePair) new BasicNameValuePair("page", "4"),
            new BasicNameValuePair("page_size", "20"),
            new BasicNameValuePair("start_date", "2010-10-11"),
            new BasicNameValuePair("end_date", "2011-10-11"),
            new BasicNameValuePair("from", "12345,6789"),
            new BasicNameValuePair("tags", "tag1,таг2")));
  }

  @Test(expected = IllegalStateException.class)
  public void rejectsFromWithComma() throws Exception {
    SinchSMSApi.batchFilter().addSender("hello,world").build();
  }

  @Test(expected = IllegalStateException.class)
  public void rejectsTagWithComma() throws Exception {
    SinchSMSApi.batchFilter().addTag("hello,world").build();
  }

  @Property
  public void generatesValidQueryParameters(
      int page,
      int pageSize,
      Set<String> senders,
      Set<String> tags,
      LocalDate startDate,
      LocalDate endDate)
      throws Exception {
    // Constrain `senders` and `tags` to strings not containing ','
    assumeThat(senders, not(hasItem(containsString(","))));
    assumeThat(tags, not(hasItem(containsString(","))));

    BatchFilter filter =
        SinchSMSApi.batchFilter()
            .pageSize(pageSize)
            .senders(senders)
            .tags(tags)
            .startDate(startDate)
            .endDate(endDate)
            .build();

    List<NameValuePair> params = filter.toQueryParams(page);

    // Will throw IllegalArgumentException if an invalid URI is attempted.
    new URIBuilder().addParameters(params).build();
  }
}
