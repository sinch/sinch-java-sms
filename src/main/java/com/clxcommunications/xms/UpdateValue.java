package com.clxcommunications.xms;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Wrapper class for API objects used for update API methods. Allows
 * 
 * @param <T>
 *            the wrapped type
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonInclude(Include.NON_NULL)
public abstract class UpdateValue<T> {

	/**
	 * Marker object signifying that the field should be removed.
	 */
	private static final UpdateValue<?> UNSET = UpdateValueImpl.of(null);

	/**
	 * An update value indicating that the value should be reset to its default
	 * value.
	 * 
	 * @return an update indicating a reset
	 * @param <T>
	 *            the wrapped type
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> UpdateValue<T> unset() {
		return (UpdateValue<T>) UNSET;
	}

	/**
	 * An update value indicating that the value should be changed.
	 * 
	 * @param value
	 *            the new value
	 * @return an update indicating a set
	 * @param <T>
	 *            the wrapped type
	 */
	@JsonCreator
	@Nonnull
	public static <T> UpdateValue<T> set(@Nullable T value) {
		if (value == null) {
			return UpdateValue.<T> unset();
		} else {
			return UpdateValueImpl.of(value);
		}
	}

	/**
	 * Returns the update value or <code>null</code> if unset.
	 * 
	 * @return the update value if set, <code>null</code> otherwise
	 */
	@JsonValue
	@Nullable
	public abstract T valueOrNull();

	/**
	 * Whether this update value is set or unset.
	 * 
	 * @return <code>true</code> if set; <code>false</code> otherwise
	 */
	public final boolean isSet() {
		return valueOrNull() != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("UpdateValue{");
		if (isSet()) {
			sb.append("set=" + valueOrNull());
		} else {
			sb.append("unset");
		}
		sb.append("}");

		return sb.toString();
	}

}
