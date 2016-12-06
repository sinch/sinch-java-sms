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

	/**
	 * A builder of group filters.
	 */
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

	@Value.Check
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
