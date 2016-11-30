package com.clxcommunications.xms.api;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Page<T> implements Iterable<T> {

	/**
	 * The page number of this page.
	 * 
	 * @return the page number
	 */
	public abstract int page();

	/**
	 * The number of elements in this page.
	 * 
	 * @return the page size
	 */
	@JsonProperty("page_size")
	public abstract int size();

	/**
	 * The total number of elements across all pages.
	 * 
	 * @return the total number of elements
	 */
	@JsonProperty("count")
	public abstract int totalSize();

	public abstract List<T> content();

	/**
	 * Returns an iterator that traverses the content of this page.
	 */
	@Override
	@JsonIgnore
	public Iterator<T> iterator() {
		return content().iterator();
	}

	/**
	 * Whether this page is empty, i.e., does not contain any elements.
	 * 
	 * @return <code>true</code> if empty, <code>false</code> otherwise
	 */
	@JsonIgnore
	public final boolean isEmpty() {
		return size() == 0;
	}

}
