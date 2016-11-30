package com.clxcommunications.xms.api;

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
