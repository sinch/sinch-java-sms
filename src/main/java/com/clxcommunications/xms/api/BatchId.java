package com.clxcommunications.xms.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Representation of a batch identifier. These are in the JSON representation
 * simply string literals but they are within the Java API represented in a more
 * type safe way using this class.
 * <p>
 * To fetch the literal batch identifier use the {@link #toString()} method.
 */
@Value.Immutable
@ValueStylePackageDirect
@ParametersAreNonnullByDefault
public abstract class BatchId implements Comparable<BatchId> {

	/**
	 * Builds an immutable {@link BatchId} having the given literal
	 * representation.
	 * 
	 * @param id
	 *            the literal batch identifier
	 * @return a batch identifier
	 */
	@JsonCreator
	@Nonnull
	public static BatchId of(String id) {
		return BatchIdImpl.of(id);
	}

	/**
	 * The literal string representation of this batch identifier.
	 * 
	 * @return a non-null identifier
	 */
	protected abstract String id();

	@Override
	public int compareTo(BatchId o) {
		return id().compareTo(o.id());
	}

	/**
	 * The string representation of this batch identifier.
	 * 
	 * @return a non-null string
	 */
	@JsonValue
	@Override
	public String toString() {
		return id();
	}

}
