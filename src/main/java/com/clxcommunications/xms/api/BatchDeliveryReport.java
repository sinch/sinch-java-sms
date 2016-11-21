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
 * Representation of a batch delivery report.
 */
@Value.Enclosing
@Value.Immutable
@ValueStylePublic
@JsonDeserialize(builder = BatchDeliveryReportImpl.Builder.class)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("delivery_report_sms")
public interface BatchDeliveryReport {

	@Value.Immutable
	@ValueStylePublic
	@JsonDeserialize(builder = BatchDeliveryReportImpl.Status.Builder.class)
	@JsonInclude(Include.NON_EMPTY)
	public interface Status {

		int code();

		DeliveryStatus status();

		int count();

		/**
		 * The recipients having this status. Note, this is non-empty only if a
		 * <em>full</em> delivery report has been requested.
		 * 
		 * @return a non-null list of recipients
		 */
		List<String> recipients();

	}

	@JsonProperty("batch_id")
	BatchId batchId();

	@JsonProperty("total_message_count")
	int totalMessageCount();

	List<Status> statuses();

}
