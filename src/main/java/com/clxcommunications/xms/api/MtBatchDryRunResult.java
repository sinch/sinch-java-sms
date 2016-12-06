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

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * The result of a message batch dry-run. The XMS API allows users to simulate
 * sending batches and this result class contains information that allow you to
 * analyze various aspects of a batch run before it is actually performed.
 * <p>
 * It is, for example possible to see how many actual SMS messages will be sent
 * as part of the batch.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchDryRunResult.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public interface MtBatchDryRunResult {

	/**
	 * A builder of batch dry-run results.
	 */
	public static final class Builder extends MtBatchDryRunResultImpl.Builder {

	}

	/**
	 * A per-recipient dry-run result.
	 */
	@Value.Immutable
	@JsonDeserialize(builder = PerRecipient.Builder.class)
	public interface PerRecipient {

		/**
		 * A builder of per-recipient dry-run results.
		 */
		public static final class Builder extends PerRecipientImpl.Builder {

		}

		/**
		 * The recipient to whom this result refers.
		 * 
		 * @return an MSISDN
		 */
		String recipient();

		/**
		 * The number of message parts that were sent to this recipient.
		 * 
		 * @return a positive integer
		 */
		@JsonProperty("number_of_parts")
		int numberOfParts();

		/**
		 * The message body sent to this recipient. This includes template
		 * expansion.
		 * 
		 * @return a message body
		 */
		String body();

		/**
		 * The message encoding that will be applied for this recipient. This is
		 * one of "text" or "unicode".
		 * 
		 * @return a string describing the encoding
		 */
		String encoding();

	}

	/**
	 * The total number of batch recipients.
	 * 
	 * @return a non-negative number
	 */
	@JsonProperty("number_of_recipients")
	int numberOfRecipients();

	/**
	 * The total number of individual messages of the batch.
	 * 
	 * @return a non-negative number
	 */
	@JsonProperty("number_of_messages")
	int numberOfMessages();

	/**
	 * Information on a per-recipient level. When requesting a dry-run this will
	 * be populated in the response only if per-recipient information is also
	 * requested and only if the batch is textual.
	 * 
	 * @return a, possibly empty, list of per-recipient batch data
	 */
	@JsonProperty("per_recipient")
	List<PerRecipient> perRecipient();

}
