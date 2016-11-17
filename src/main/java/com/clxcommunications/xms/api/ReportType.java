package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Describes the different types of delivery reports supported by XMS.
 */
@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class ReportType {

	/**
	 * No delivery report is desired or available.
	 */
	public static final ReportType NONE =
	        ReportTypeImpl.of("none");

	/**
	 * A summary delivery report is desired or available.
	 */
	public static final ReportType SUMMARY =
	        ReportTypeImpl.of("summary");

	/**
	 * A full delivery report is desired or available.
	 */
	public static final ReportType FULL =
	        ReportTypeImpl.of("full");

	/**
	 * A per recipient delivery report is desired or available.
	 */
	public static final ReportType PER_RECIPIENT =
	        ReportTypeImpl.of("per_recipient");

	@JsonValue
	public abstract String type();

	@JsonCreator
	public static ReportType of(String type) {
		return ReportTypeImpl.of(type);
	}

}
