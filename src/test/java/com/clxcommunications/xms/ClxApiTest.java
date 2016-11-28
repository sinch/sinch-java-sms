package com.clxcommunications.xms;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

public class ClxApiTest {

	@Test
	public void canFilterTodaysBatches() throws Exception {
		LocalDate today = LocalDate.now(Clock.systemUTC());

		List<NameValuePair> actual =
		        ClxApi.filterTodaysBatches().toQueryParams(81);

		assertThat(actual, containsInAnyOrder(
		        (NameValuePair) new BasicNameValuePair("page", "81"),
		        new BasicNameValuePair("start_date", today.toString())));
	}

	@Test
	public void canFilterYesterdaysBatches() throws Exception {
		LocalDate today = LocalDate.now(Clock.systemUTC());
		LocalDate yesterday = today.minusDays(1);
		List<NameValuePair> actual =
		        ClxApi.filterYesterdaysBatches().toQueryParams(82);

		assertThat(actual, containsInAnyOrder(
		        (NameValuePair) new BasicNameValuePair("page", "82"),
		        new BasicNameValuePair("start_date", yesterday.toString()),
		        new BasicNameValuePair("end_date", today.toString())));
	}

}
