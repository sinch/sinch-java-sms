package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

@Value.Immutable
@ValueStylePublic
public abstract class GroupFilter {

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

	public abstract Set<String> tags();

	protected void check() {
		for (String s : tags()) {
			if (s.contains(",")) {
				throw new IllegalArgumentException("tags contains comma");
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

		if (!tags().isEmpty()) {
			params.add(new BasicNameValuePair("tags",
			        Utils.join(",", tags())));
		}

		return URLEncodedUtils.format(params, Consts.UTF_8);
	}

}
