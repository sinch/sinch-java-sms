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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Describes the different types of delivery reports supported by XMS.
 * <p>
 * A number of predefined delivery statuses are provided as constants within
 * this class, for example, {@link #QUEUED} or {@link #FAILED}, but XMS reserves
 * the right to add new codes in the future.
 */
@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class DeliveryStatus {

	/**
	 * Message is queued within REST API system and will be dispatched according
	 * to the rate of the account.
	 */
	public static final DeliveryStatus QUEUED =
	        DeliveryStatusImpl.of("Queued");

	/**
	 * Message has been dispatched and accepted for delivery by the SMSC.
	 */
	public static final DeliveryStatus DISPATCHED =
	        DeliveryStatusImpl.of("Dispatched");

	/**
	 * Message was aborted before reaching SMSC.
	 */
	public static final DeliveryStatus ABORTED =
	        DeliveryStatusImpl.of("Aborted");

	/**
	 * Message was rejected by SMSC.
	 */
	public static final DeliveryStatus REJECTED =
	        DeliveryStatusImpl.of("Rejected");

	/**
	 * Message has been delivered.
	 */
	public static final DeliveryStatus DELIVERED =
	        DeliveryStatusImpl.of("Delivered");

	/**
	 * Message failed to be delivered.
	 */
	public static final DeliveryStatus FAILED =
	        DeliveryStatusImpl.of("Failed");

	/**
	 * Message expired before delivery.
	 */
	public static final DeliveryStatus EXPIRED =
	        DeliveryStatusImpl.of("Expired");

	/**
	 * It is not known if message was delivered or not.
	 */
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
