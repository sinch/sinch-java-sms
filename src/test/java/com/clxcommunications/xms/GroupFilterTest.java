package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class GroupFilterTest {

	@Test
	public void canGenerateQueryParameters() throws Exception {
		GroupFilter filter = ClxApi.buildGroupFilter()
		        .pageSize(20)
		        .addTag("tag1", "таг2")
		        .build();

		String actual = filter.toUrlEncodedQuery(4);
		String expected = "page=4"
		        + "&page_size=20"
		        + "&tags=tag1%2C%D1%82%D0%B0%D0%B32";

		assertThat(actual, is(expected));
	}

	@Property(trials = 50)
	public void generatesValidQueryParameters(int page, int pageSize,
	        Set<String> tags) throws Exception {
		GroupFilter filter = ClxApi.buildGroupFilter()
		        .pageSize(pageSize)
		        .tags(tags)
		        .build();

		String query = filter.toUrlEncodedQuery(page);

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		URI.create("http://localhost/?" + query);
	}

}
