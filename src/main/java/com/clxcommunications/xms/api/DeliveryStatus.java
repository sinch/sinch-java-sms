package com.clxcommunications.xms.api;

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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Describes the different types of delivery reports supported by XMS.
 */
@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class DeliveryStatus {

	public static final DeliveryStatus QUEUED =
	        DeliveryStatusImpl.of("Queued");

	public static final DeliveryStatus DISPATCHED =
	        DeliveryStatusImpl.of("Dispatched");

	public static final DeliveryStatus ABORTED =
	        DeliveryStatusImpl.of("Aborted");

	public static final DeliveryStatus REJECTED =
	        DeliveryStatusImpl.of("Rejected");

	public static final DeliveryStatus DELIVERED =
	        DeliveryStatusImpl.of("Delivered");

	public static final DeliveryStatus FAILED =
	        DeliveryStatusImpl.of("Failed");

	public static final DeliveryStatus EXPIRED =
	        DeliveryStatusImpl.of("Expired");

	public static final DeliveryStatus UNKNOWN =
	        DeliveryStatusImpl.of("Unknown");

	/**
	 * The string representation of this delivery status.
	 * 
	 * @return a non-null string
	 */
	@JsonValue
	public abstract String status();

	/**
	 * Creates a delivery status object from the given string representation.
	 * 
	 * @param status
	 *            string describing the status
	 * @return a non-null delivery status object
	 */
	@JsonCreator
	public static DeliveryStatus of(String status) {
		return DeliveryStatusImpl.of(status);
	}

}
