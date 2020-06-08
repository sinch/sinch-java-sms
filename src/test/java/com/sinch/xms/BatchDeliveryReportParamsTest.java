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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sinch.xms.api.DeliveryStatus;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class BatchDeliveryReportParamsTest {

	@Test
	public void generatesExpectedQueryParametersMinimal() throws Exception {
		BatchDeliveryReportParams filter =
		        SinchSMSApi.batchDeliveryReportParams().build();

		List<NameValuePair> actual = filter.toQueryParams();

		assertThat(actual, is(empty()));
	}

	@Test
	public void generatesExpectedQueryParametersMaximalish() throws Exception {
		BatchDeliveryReportParams filter =
		        SinchSMSApi.batchDeliveryReportParams()
		                .fullReport()
		                .addStatus(DeliveryStatus.EXPIRED,
		                        DeliveryStatus.DELIVERED)
		                .addCode(100, 200, 300)
		                .build();

		List<NameValuePair> actual = filter.toQueryParams();

		assertThat(actual, containsInAnyOrder(
		        (NameValuePair) new BasicNameValuePair("type", "full"),
		        new BasicNameValuePair("status", "Expired,Delivered"),
		        new BasicNameValuePair("code", "100,200,300")));
	}

	@Property
	public void generatesValidQueryParameters(
	        BatchDeliveryReportParams.ReportType reportType,
	        Set<Integer> codes) throws Exception {
		BatchDeliveryReportParams filter =
		        SinchSMSApi.batchDeliveryReportParams()
		                .reportType(reportType)
		                .addStatus(DeliveryStatus.EXPIRED,
		                        DeliveryStatus.DELIVERED)
		                .codes(codes)
		                .build();

		List<NameValuePair> params = filter.toQueryParams();

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		new URIBuilder().addParameters(params).build();
	}

}
