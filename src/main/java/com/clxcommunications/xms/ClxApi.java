package com.clxcommunications.xms;

import javax.annotation.Nonnull;

import com.clxcommunications.xms.api.GroupCreate;
import com.clxcommunications.xms.api.GroupUpdate;
import com.clxcommunications.xms.api.MtBatchBinarySmsCreate;
import com.clxcommunications.xms.api.MtBatchBinarySmsUpdate;
import com.clxcommunications.xms.api.MtBatchTextSmsCreate;
import com.clxcommunications.xms.api.MtBatchTextSmsUpdate;
import com.clxcommunications.xms.api.ParameterValues;
import com.clxcommunications.xms.api.TagsUpdate;

/**
 * A collection of convenient static methods for building XMS objects. Each
 * method here will return a fresh builder producing the type of XMS object
 * indicated by the method name.
 */
public final class ClxApi { // TODO Maybe rename this to, e.g., "ApiBuilders"?

	/**
	 * Returns a freshly created batch SMS builder for text messages.
	 * 
	 * @return a builder of text batch messages
	 */
	@Nonnull
	public static MtBatchTextSmsCreate.Builder batchTextSms() {
		return new MtBatchTextSmsCreate.Builder();
	}

	/**
	 * Returns a freshly created builder for text message updates.
	 * 
	 * @return a builder of text message updates
	 */
	@Nonnull
	public static MtBatchTextSmsUpdate.Builder batchTextSmsUpdate() {
		return new MtBatchTextSmsUpdate.Builder();
	}

	/**
	 * Returns a freshly created batch SMS builder for binary messages.
	 * 
	 * @return a builder of binary batch messages
	 */
	@Nonnull
	public static MtBatchBinarySmsCreate.Builder batchBinarySms() {
		return new MtBatchBinarySmsCreate.Builder();
	}

	/**
	 * Returns a freshly created builder for binary message updates.
	 * 
	 * @return a builder of binary message updates
	 */
	@Nonnull
	public static MtBatchBinarySmsUpdate.Builder batchBinarySmsUpdate() {
		return new MtBatchBinarySmsUpdate.Builder();
	}

	/**
	 * Returns a freshly created builder of text message parameter
	 * substitutions.
	 * 
	 * @return a builder of text parameter substitutions
	 */
	@Nonnull
	public static ParameterValues.Builder parameterValues() {
		return new ParameterValues.Builder();
	}

	/**
	 * Returns a freshly created builder of groups.
	 * 
	 * @return a builder of groups
	 */
	@Nonnull
	public static GroupCreate.Builder groupCreate() {
		return new GroupCreate.Builder();
	}

	/**
	 * Returns a freshly created builder of group updates.
	 * 
	 * @return a builder of group updates
	 */
	@Nonnull
	public static GroupUpdate.Builder groupUpdate() {
		return new GroupUpdate.Builder();
	}

	/**
	 * Returns a freshly created tags update builder.
	 * 
	 * @return a builder of tags update requests
	 */
	@Nonnull
	public static TagsUpdate.Builder tagsUpdate() {
		return new TagsUpdate.Builder();
	}

	/**
	 * Returns a freshly created builder of batch filters.
	 * 
	 * @return a builder of batch filters
	 */
	@Nonnull
	public static BatchFilter.Builder batchFilter() {
		return new BatchFilter.Builder();
	}

	/**
	 * Returns a freshly created builder of group filters.
	 * 
	 * @return a builder of group filters
	 */
	@Nonnull
	public static GroupFilter.Builder groupFilter() {
		return new GroupFilter.Builder();
	}

	/**
	 * Returns a freshly created builder of inbound message filters.
	 * 
	 * @return a builder of inbound message filters
	 */
	@Nonnull
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
