package com.clxcommunications.xms.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Representation of a delivery report for a specific recipient.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = RecipientDeliveryReport.Builder.class)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("recipient_delivery_report_sms")
public interface RecipientDeliveryReport {

	public static final class Builder
	        extends RecipientDeliveryReportImpl.Builder {

	}

	@JsonProperty("batch_id")
	BatchId batchId();

	String recipient();

	int code();

	DeliveryStatus status();

	@JsonProperty("status_message")
	@Nullable
	String statusMessage();

	@Nullable
	String operator();

	OffsetDateTime at();

	@JsonProperty("operator_status_at")
	OffsetDateTime operatorStatusAt();

}
