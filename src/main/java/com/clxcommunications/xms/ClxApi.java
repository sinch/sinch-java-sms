package com.clxcommunications.xms;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;

import com.clxcommunications.xms.api.MtBatchBinarySmsCreate;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.ParameterValues;

/**
 * A collection of convenient static methods for generating CLX API objects.
 */
public final class ClxApi { // TODO Maybe rename this to, e.g., "Xms"?

	public static MtBatchTextSmsCreate.Builder batchTextSms() {
		return new MtBatchTextSmsCreate.Builder();
	}

	public static MtBatchTextSmsUpdate.Builder batchTextSmsUpdate() {
		return new MtBatchTextSmsUpdate.Builder();
	}

	public static MtBatchBinarySmsCreate.Builder batchBinarySms() {
		return new MtBatchBinarySmsCreate.Builder();
	}

	public static MtBatchBinarySmsUpdate.Builder batchBinarySmsUpdate() {
		return new MtBatchBinarySmsUpdate.Builder();
	}

	public static ParameterValues.Builder buildSubstitution() {
		return new ParameterValues.Builder();
	}

	public static BatchFilter.Builder batchFilter() {
		return new BatchFilter.Builder();
	}

	public static BatchFilter filterTodaysBatches() {
		return new BatchFilter.Builder()
		        .startDate(LocalDate.now(Clock.systemUTC()))
		        .build();
	}

	public static BatchFilter filterYesterdaysBatches() {
		final LocalDate now = LocalDate.now(Clock.systemUTC());
		return new BatchFilter.Builder()
		        .startDate(now.minusDays(1))
		        .endDate(now)
		        .build();
	}

	public static GroupFilter.Builder groupFilter() {
		return new GroupFilter.Builder();
	}

	public static InboundsFilter.Builder inboundsFilter() {
		return new InboundsFilter.Builder();
	}

}
