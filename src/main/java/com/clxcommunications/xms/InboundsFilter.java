package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;
import org.threeten.bp.LocalDate;

/**
 * Describes a filter for limiting results when fetching inbound messages.
 */
@Value.Immutable
@ValueStylePublic
public abstract class InboundsFilter {

	/**
	 * The requested number of entries per page. A non-positive value means that
	 * the default value will be used.
	 * 
	 * @return the desired page size
	 */
	@Value.Default
	public int pageSize() {
		return 0;
	}

	/**
	 * Limits results to inbound messages sent at or after this date.
	 * 
	 * @return a date
	 */
	@Nullable
	public abstract LocalDate startDate();

	/**
	 * Limits results to inbound messages send before this date.
	 * 
	 * @return a date
	 */
	@Nullable
	public abstract LocalDate endDate();

	/**
	 * Limits results to inbound messages destined for one of these numbers. If
	 * empty then all message destinations are eligible.
	 * 
	 * @return a set of message destinations
	 */
	public abstract Set<String> to();

	/**
	 * Verifies that this filter is in a reasonable state.
	 */
	protected void check() {
		for (String s : to()) {
			if (s.contains(",")) {
				throw new IllegalStateException("to contains comma");
			}
		}
	}

	/**
	 * Formats this filter as an URL encoded list of query parameters.
	 * 
	 * @param page
	 *            the page to request
	 * @return a non-null string containing query parameters
	 */
	@Nonnull
	String toUrlEncodedQuery(int page) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(6);

		params.add(new BasicNameValuePair("page", String.valueOf(page)));

		if (pageSize() > 0) {
			params.add(new BasicNameValuePair("page_size",
			        String.valueOf(pageSize())));
		}

		if (startDate() != null) {
			params.add(new BasicNameValuePair("start_date",
			        startDate().toString()));
		}

		if (endDate() != null) {
			params.add(
			        new BasicNameValuePair("end_date", endDate().toString()));
		}

		if (!to().isEmpty()) {
			params.add(new BasicNameValuePair("to", Utils.join(",", to())));
		}

		return URLEncodedUtils.format(params, Consts.UTF_8);
	}

}
