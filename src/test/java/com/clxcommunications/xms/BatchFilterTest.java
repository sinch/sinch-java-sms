package com.clxcommunications.xms;

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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class BatchFilterTest {

	@Test
	public void canGenerateQueryParameters() throws Exception {
		BatchFilter filter = ClxApi.batchFilter()
		        .pageSize(20)
		        .addFrom("12345", "6789")
		        .addTag("tag1", "таг2")
		        .startDate(LocalDate.of(2010, 10, 11))
		        .endDate(LocalDate.of(2011, 10, 11))
		        .build();

		List<NameValuePair> actual = filter.toQueryParams(4);

		assertThat(actual, containsInAnyOrder(
		        (NameValuePair) new BasicNameValuePair("page", "4"),
		        new BasicNameValuePair("page_size", "20"),
		        new BasicNameValuePair("start_date", "2010-10-11"),
		        new BasicNameValuePair("end_date", "2011-10-11"),
		        new BasicNameValuePair("from", "12345,6789"),
		        new BasicNameValuePair("tags", "tag1,таг2")));
	}

	@Property
	public void generatesValidQueryParameters(int page, int pageSize,
	        Set<String> from, Set<String> tags, LocalDate startDate,
	        LocalDate endDate) throws Exception {
		BatchFilter filter = ClxApi.batchFilter()
		        .pageSize(pageSize)
		        .from(from)
		        .tags(tags)
		        .startDate(startDate)
		        .endDate(endDate)
		        .build();

		List<NameValuePair> params = filter.toQueryParams(page);

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		new URIBuilder().addParameters(params).build();
	}

}
