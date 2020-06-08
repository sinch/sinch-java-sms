/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
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
package com.sinch.xms.api;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A representation of a set of tags.
 */
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
