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

@RunWith(JUnitQuickcheck.class)
public class GroupFilterTest {

  @Test
  public void canGenerateMinimal() throws Exception {
    GroupFilter filter = SinchSMSApi.groupFilter().build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(actual, containsInAnyOrder((NameValuePair) new BasicNameValuePair("page", "4")));
  }

  @Test
  public void canGenerateQueryParameters() throws Exception {
    GroupFilter filter = SinchSMSApi.groupFilter().pageSize(20).addTag("tag1", "таг2").build();

    List<NameValuePair> actual = filter.toQueryParams(4);

    assertThat(
        actual,
        containsInAnyOrder(
            (NameValuePair) new BasicNameValuePair("page", "4"),
            new BasicNameValuePair("page_size", "20"),
            new BasicNameValuePair("tags", "tag1,таг2")));
  }

  @Test(expected = IllegalStateException.class)
  public void rejectsTagWithComma() throws Exception {
    SinchSMSApi.groupFilter().addTag("hello,world").build();
  }

  @Property
  public void generatesValidQueryParameters(int page, int pageSize, Set<String> tags)
      throws Exception {
    // Constrain `tags` to strings not containing ','
    assumeThat(tags, not(hasItem(containsString(","))));

    GroupFilter filter = SinchSMSApi.groupFilter().pageSize(pageSize).tags(tags).build();

    List<NameValuePair> params = filter.toQueryParams(page);

    // Will throw IllegalArgumentException if an invalid URI is attempted.
    new URIBuilder().addParameters(params).build();
  }
}
