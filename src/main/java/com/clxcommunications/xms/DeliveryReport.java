package com.clxcommunications.xms;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
@JsonSerialize(using = JacksonUtils.DeliveryReportSerializer.class)
@JsonDeserialize(using = JacksonUtils.DeliveryReportDeserializer.class)
public interface DeliveryReport {

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

	String style();

}
