/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
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
package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * A batch delivery report.
 */
@Value.Enclosing
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = BatchDeliveryReport.Builder.class)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonTypeName("delivery_report_sms")
public abstract class BatchDeliveryReport {

	/**
	 * A description of the messages having a given delivery state.
	 */
	@Value.Immutable
	@JsonDeserialize(builder = BatchDeliveryReport.Status.Builder.class)
	@JsonInclude(Include.NON_EMPTY)
	public static abstract class Status {

		/**
		 * A builder of batch delivery report statuses.
		 */
		public static class Builder extends BatchDeliveryReportImpl.Status.Builder {

			Builder() {
			}

		}

		/**
		 * Creates a builder of {@link Status} instances.
		 * 
		 * @return a builder
		 */
		@Nonnull
		public static final Status.Builder builder() {
			return new Builder();
		}

		/**
		 * The delivery status code.
		 * 
		 * @return a status code
		 */
		public abstract int code();

		/**
		 * The delivery status for this bucket.
		 * 
		 * @return a non-null delivery status
		 */
		public abstract DeliveryStatus status();

		/**
		 * The number of individual messages in this status bucket.
		 * 
		 * @return a positive integer
		 */
		public abstract int count();

		/**
		 * The recipients having this status. Note, this is non-empty only if a
		 * <em>full</em> delivery report has been requested.
		 * 
		 * @return a non-null list of recipients
		 */
		public abstract List<String> recipients();

	}

	/**
	 * A builder of batch delivery reports.
	 */
	public static class Builder extends BatchDeliveryReportImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link BatchDeliveryReport} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final BatchDeliveryReport.Builder builder() {
		return new Builder();
	}

	/**
	 * Identifier of the batch to which this delivery report refers.
	 * 
	 * @return a non-null batch identifier
	 */
	@JsonProperty("batch_id")
	public abstract BatchId batchId();

	/**
	 * The total number of messages in the batch. This is including message
	 * expansion, that is, including messages needing multiple parts.
	 * 
	 * @return a positive integer
	 */
	@JsonProperty("total_message_count")
	public abstract int totalMessageCount();

	/**
	 * A list of {@link Status statuses} for the batch. Only non-empty statuses are
	 * present here, that is, for each member status there is at least one message
	 * having the state.
	 * 
	 * @return a list of statuses
	 */
	public abstract List<Status> statuses();

	/**
	 * The optional client identifier attached to this message.
	 * 
	 * @return a client reference id
	 */
	@Nullable
	@JsonProperty("client_reference")
	public abstract String clientReference();

}
