package com.clxcommunications.xms;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

/**
 * A collection of convenient static methods for generating CLX API objects.
 */
public final class ClxApi {

	public static final int MAX_BODY_BYTES = 140;

	public static ImmutableMtBatchTextSms.Builder buildBatchTextSms() {
		return ImmutableMtBatchTextSms.builder();
	}

	public static ImmutableMtBatchBinarySms.Builder buildBatchBinarySms() {
		return ImmutableMtBatchBinarySms.builder();
	}

	public static ImmutableParameterValues.Builder buildSubstitution() {
		return ImmutableParameterValues.builder();
	}

	public static ImmutableBatchFilter.Builder buildBatchFilter() {
		return ImmutableBatchFilter.builder();
	}

	public static BatchFilter filterTodaysBatches() {
		return ImmutableBatchFilter.builder()
		        .startDate(LocalDate.now(Clock.systemUTC()))
		        .build();
	}

	public static BatchFilter filterYesterdaysBatches() {
		final LocalDate now = LocalDate.now(Clock.systemUTC());
		return ImmutableBatchFilter.builder()
		        .startDate(now.minusDays(1))
		        .endDate(now)
		        .build();
	}

}
