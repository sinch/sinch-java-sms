package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class DeliveryReport {

	/**
	 * No delivery report is desired or available.
	 */
	public static final DeliveryReport NONE =
	        ImmutableDeliveryReport.of("none");

	/**
	 * A summary delivery report is desired or available.
	 */
	public static final DeliveryReport SUMMARY =
	        ImmutableDeliveryReport.of("summary");

	/**
	 * A full delivery report is desired or available.
	 */
	public static final DeliveryReport FULL =
	        ImmutableDeliveryReport.of("full");

	/**
	 * A per recipient delivery report is desired or available.
	 */
	public static final DeliveryReport PER_RECIPIENT =
	        ImmutableDeliveryReport.of("per_recipient");

	@JsonValue
	public abstract String style();

	@JsonCreator
	public static DeliveryReport of(String style) {
		return ImmutableDeliveryReport.of(style);
	}

}
