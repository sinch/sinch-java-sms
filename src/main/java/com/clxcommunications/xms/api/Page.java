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
	 * The total number of pages.
	 * 
	 * @return the number of pages
	 */
	@JsonProperty("count")
	public abstract int numPages();

	public abstract List<T> content();

	/**
	 * Returns an iterator that traverses the content of this page.
	 */
	@Override
	@JsonIgnore
	public Iterator<T> iterator() {
		return content().iterator();
	}

	@JsonIgnore
	public final boolean isLast() {
		return page() + 1 >= numPages();
	}

}
