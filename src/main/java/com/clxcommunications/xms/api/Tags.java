package com.clxcommunications.xms.api;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = TagsImpl.Builder.class)
@ParametersAreNonnullByDefault
public abstract class Tags implements Iterable<String> {

	/**
	 * A set representation of this collection of tags.
	 * 
	 * @return an unmodifiable set of tag strings
	 */
	@Nonnull
	public abstract Set<String> tags();

	/**
	 * Constructs a new tag collection containing the given tags.
	 * 
	 * @param tags
	 *            tags to place in the tag set
	 * @return a collection of tags
	 */
	@Nonnull
	public static final Tags of(Iterable<String> tags) {
		return TagsImpl.builder().addAllTags(tags).build();
	}

	/**
	 * Constructs a new tag collection containing the given tags.
	 * 
	 * @param tags
	 *            tags to place in the tag set
	 * @return a collection of tags
	 */
	@Nonnull
	public static final Tags of(String... tags) {
		return TagsImpl.builder().addTag(tags).build();
	}

	/**
	 * Returns an iterator over this set of tags.
	 * 
	 * @return an iterator yielding tag strings
	 */
	@Override
	public Iterator<String> iterator() {
		return tags().iterator();
	}

	@Override
	public String toString() {
		return tags().toString();
	}

}
