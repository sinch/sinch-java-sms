package com.clxcommunications.xms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Wrapper class for API objects used for update API methods. Allows
 * 
 * @param <T>
 *            the underlying API type
 */
@JsonInclude(Include.NON_NULL)
public final class UpdateValue<T> {

	/**
	 * Marker object signifying that the field should be removed.
	 */
	private static final UpdateValue<?> UNSET = new UpdateValue<Object>(null);

	@Nullable
	private final T value;

	private UpdateValue(T value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> UpdateValue<T> unset() {
		return (UpdateValue<T>) UNSET;
	}

	@JsonCreator
	@Nonnull
	public static <T> UpdateValue<T> set(@Nullable T value) {
		return new UpdateValue<T>(value);
	}

	/**
	 * Returns the update value or <code>null</code> if unset.
	 * 
	 * @return the update value if set, <code>null</code> otherwise
	 */
	@JsonValue
	public T valueOrNull() {
		return value;
	}

}
