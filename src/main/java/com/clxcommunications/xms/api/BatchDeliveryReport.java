package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A batch delivery report.
 */
@Value.Enclosing
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = BatchDeliveryReportImpl.Builder.class)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("delivery_report_sms")
public interface BatchDeliveryReport {

	/**
	 * A description of the messages having a given delivery state.
	 */
	@Value.Immutable
	@JsonDeserialize(builder = BatchDeliveryReportImpl.Status.Builder.class)
	@JsonInclude(Include.NON_EMPTY)
	public interface Status {

		/**
		 * The delivery status code.
		 * 
		 * @return a status code
		 */
		int code();

		/**
		 * The delivery status for this bucket.
		 * 
		 * @return a non-null delivery status
		 */
		DeliveryStatus status();

		/**
		 * The number of individual messages in this status bucket.
		 * 
		 * @return a positive integer
		 */
		int count();

		/**
		 * The recipients having this status. Note, this is non-empty only if a
		 * <em>full</em> delivery report has been requested.
		 * 
		 * @return a non-null list of recipients
		 */
		List<String> recipients();

	}

	/**
	 * Identifier of the batch to which this delivery report refers.
	 * 
	 * @return a non-null batch identifier
	 */
	@JsonProperty("batch_id")
	BatchId batchId();

	/**
	 * The total number of messages in the batch. This is including message
	 * expansion, that is, including messages needing multiple parts.
	 * 
	 * @return a positive integer
	 */
	@JsonProperty("total_message_count")
	int totalMessageCount();

	/**
	 * A list of {@link Status statuses} for the batch. Only non-empty statuses
	 * are present here, that is, for each member status there is at least one
	 * message having the state.
	 * 
	 * @return a list of statuses
	 */
	List<Status> statuses();

}
