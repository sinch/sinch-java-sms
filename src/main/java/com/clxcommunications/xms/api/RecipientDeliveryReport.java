/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.clxcommunications.xms.api;

import javax.annotation.Nonnull;
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
public abstract class RecipientDeliveryReport {

	/**
	 * A builder of recipient delivery reports.
	 */
	public static final class Builder
	        extends RecipientDeliveryReportImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link RecipientDeliveryReport} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final RecipientDeliveryReport.Builder builder() {
		return new Builder();
	}

	/**
	 * The batch to which this delivery report belongs
	 * 
	 * @return a batch identifier
	 */
	@JsonProperty("batch_id")
	public abstract BatchId batchId();

	/**
	 * The recipient to which this delivery report refers.
	 * 
	 * @return an MSISDN
	 */
	public abstract String recipient();

	/**
	 * The delivery report error code of the message.
	 * 
	 * @return a delivery report error code
	 */
	public abstract int code();

	/**
	 * The delivery status of the message.
	 * 
	 * @return a delivery status
	 */
	public abstract DeliveryStatus status();

	/**
	 * A description of the status, if available.
	 * 
	 * @return a status description
	 */
	@JsonProperty("status_message")
	@Nullable
	public abstract String statusMessage();

	/**
	 * The operator MCCMNC, if available.
	 * 
	 * @return an operator identifier; <code>null</code> if unknown
	 */
	@Nullable
	public abstract String operator();

	/**
	 * Time when the message reached it's final state.
	 * 
	 * @return a date and time
	 */
	public abstract OffsetDateTime at();

	/**
	 * The message timestamp as recorded by the network operator, if message dispatched.
	 * 
	 * @return a date and time if message dispatched; <code>null</code> otherwise
	 */
	@JsonProperty("operator_status_at")
	@Nullable
	public abstract OffsetDateTime operatorStatusAt();

}
