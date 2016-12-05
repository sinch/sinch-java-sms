package com.clxcommunications.xms.api;

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
