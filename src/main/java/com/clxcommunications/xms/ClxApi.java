package com.clxcommunications.xms;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

import com.clxcommunications.xms.api.MtBatchBinarySmsCreateImpl;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchTextSmsCreateImpl;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.ParameterValuesImpl;

/**
 * A collection of convenient static methods for generating CLX API objects.
 */
public final class ClxApi {

	public static final int MAX_BODY_BYTES = 140;

	public static MtBatchTextSmsCreateImpl.Builder buildBatchTextSms() {
		return MtBatchTextSmsCreateImpl.builder();
	}

	public static MtBatchTextSmsUpdate.Builder buildBatchTextSmsUpdate() {
		return MtBatchTextSmsUpdate.builder();
	}

	public static MtBatchBinarySmsCreateImpl.Builder buildBatchBinarySms() {
		return MtBatchBinarySmsCreateImpl.builder();
	}

	public static MtBatchBinarySmsUpdate.Builder buildBatchBinarySmsUpdate() {
		return MtBatchBinarySmsUpdate.builder();
	}

	public static ParameterValuesImpl.Builder buildSubstitution() {
		return ParameterValuesImpl.builder();
	}

	public static BatchFilterImpl.Builder buildBatchFilter() {
		return BatchFilterImpl.builder();
	}

	public static BatchFilter filterTodaysBatches() {
		return BatchFilterImpl.builder()
		        .startDate(LocalDate.now(Clock.systemUTC()))
		        .build();
	}

	public static BatchFilter filterYesterdaysBatches() {
		final LocalDate now = LocalDate.now(Clock.systemUTC());
		return BatchFilterImpl.builder()
		        .startDate(now.minusDays(1))
		        .endDate(now)
		        .build();
	}

	public static GroupFilterImpl.Builder buildGroupFilter() {
		return GroupFilterImpl.builder();
	}

}
