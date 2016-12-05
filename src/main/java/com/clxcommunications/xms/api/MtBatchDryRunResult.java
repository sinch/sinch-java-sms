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

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchDryRunResult.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public interface MtBatchDryRunResult {

	public static final class Builder extends MtBatchDryRunResultImpl.Builder {

	}

	@Value.Immutable
	@JsonDeserialize(builder = PerRecipient.Builder.class)
	public interface PerRecipient {

		public static final class Builder extends PerRecipientImpl.Builder {

		}

		String recipient();

		@JsonProperty("number_of_parts")
		int numberOfParts();

		String body();

		String encoding();

	}

	@JsonProperty("number_of_recipients")
	int numberOfRecipients();

	@JsonProperty("number_of_messages")
	int numberOfMessages();

	@JsonProperty("per_recipient")
	List<PerRecipient> perRecipient();

}
