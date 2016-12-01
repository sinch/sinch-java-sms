package com.clxcommunications.xms;

import javax.annotation.Nonnull;

import com.clxcommunications.xms.api.GroupCreate;
import com.clxcommunications.xms.api.GroupUpdate;
import com.clxcommunications.xms.api.MtBatchBinarySmsCreate;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.ParameterValues;

/**
 * A collection of convenient static methods for building XMS objects. Each
 * method here will return a fresh builder producing the type of XMS object
 * indicated by the method name.
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

	public static ParameterValues.Builder parameterValues() {
		return new ParameterValues.Builder();
	}

	public static GroupCreate.Builder groupCreate() {
		return new GroupCreate.Builder();
	}

	public static GroupUpdate.Builder groupUpdate() {
		return new GroupUpdate.Builder();
	}

	public static BatchFilter.Builder batchFilter() {
		return new BatchFilter.Builder();
	}

	public static GroupFilter.Builder groupFilter() {
		return new GroupFilter.Builder();
	}

	public static InboundsFilter.Builder inboundsFilter() {
		return new InboundsFilter.Builder();
	}

	/**
	 * Returns a freshly created batch delivery report parameter builder.
	 * 
	 * @return a builder of batch delivery report queries
	 */
	@Nonnull
	public static BatchDeliveryReportParams.Builder batchDeliveryReportParams() {
		return new BatchDeliveryReportParams.Builder();
	}

}
