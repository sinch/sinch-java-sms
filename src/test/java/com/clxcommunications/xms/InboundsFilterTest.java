package com.clxcommunications.xms;

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
public class InboundsFilterTest {

	@Test
	public void canGenerateQueryParameters() throws Exception {
		InboundsFilter filter = ClxApi.inboundsFilter()
		        .pageSize(20)
		        .startDate(LocalDate.of(2010, 11, 12))
		        .endDate(LocalDate.of(2011, 11, 12))
		        .build();

		List<NameValuePair> actual = filter.toQueryParams(4);

		assertThat(actual, containsInAnyOrder(
		        (NameValuePair) new BasicNameValuePair("page", "4"),
		        new BasicNameValuePair("page_size", "20"),
		        new BasicNameValuePair("start_date", "2010-11-12"),
		        new BasicNameValuePair("end_date", "2011-11-12")));
	}

	@Property
	public void generatesValidQueryParameters(int page, int pageSize,
	        Set<String> tags, LocalDate startDate, LocalDate endDate)
	        throws Exception {
		InboundsFilter filter = ClxApi.inboundsFilter()
		        .pageSize(pageSize)
		        .startDate(startDate)
		        .endDate(endDate)
		        .build();

		List<NameValuePair> params = filter.toQueryParams(page);

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		new URIBuilder().addParameters(params).build();
	}

}
