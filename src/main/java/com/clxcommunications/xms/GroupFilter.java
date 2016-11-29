package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

/**
 * Describes a filter for limiting results when fetching groups.
 */
@Value.Immutable
@ValueStylePackage
public abstract class GroupFilter {

	public static class Builder extends GroupFilterImpl.Builder {

	}

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
	 * If non-empty then only groups with one or more of these tags are
	 * retained. If empty then tags are not considered in the filtering.
	 * 
	 * @return a non-null set of tags.
	 */
	public abstract Set<String> tags();

	protected void check() {
		for (String s : tags()) {
			if (s.contains(",")) {
				throw new IllegalStateException("tags contains comma");
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
	List<NameValuePair> toQueryParams(int page) {
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

		return params;
	}

}
