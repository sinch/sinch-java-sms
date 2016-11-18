package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class InboundsFilterTest {

	@Test
	public void canGenerateQueryParameters() throws Exception {
		InboundsFilter filter = ClxApi.buildInboundsFilter()
		        .pageSize(20)
		        .startDate(LocalDate.of(2010, 10, 10))
		        .endDate(LocalDate.of(2011, 10, 10))
		        .build();

		String actual = filter.toUrlEncodedQuery(4);
		String expected = "page=4"
		        + "&page_size=20"
		        + "&start_date=2010-10-10"
		        + "&end_date=2011-10-10";

		assertThat(actual, is(expected));
	}

	@Property
	public void generatesValidQueryParameters(int page, int pageSize,
	        Set<String> tags, LocalDate startDate, LocalDate endDate)
	        throws Exception {
		InboundsFilter filter = ClxApi.buildInboundsFilter()
		        .pageSize(pageSize)
		        .startDate(startDate)
		        .endDate(endDate)
		        .build();

		String query = filter.toUrlEncodedQuery(page);

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		URI.create("http://localhost/?" + query);
	}

}
