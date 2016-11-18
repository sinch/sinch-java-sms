package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

public class ClxApiTest {

	@Test
	public void canFilterTodaysBatches() throws Exception {
		LocalDate now = LocalDate.now(Clock.systemUTC());
		String expected = String.format("page=81&start_date=%s", now);
		String actual = ClxApi.filterTodaysBatches().toUrlEncodedQuery(81);

		assertThat(actual, is(expected));
	}

	@Test
	public void canFilterYesterdaysBatches() throws Exception {
		LocalDate now = LocalDate.now(Clock.systemUTC());
		String expected = String.format("page=82&start_date=%s&end_date=%s",
		        now.minusDays(1), now);
		String actual = ClxApi.filterYesterdaysBatches().toUrlEncodedQuery(82);

		assertThat(actual, is(expected));
	}

}
