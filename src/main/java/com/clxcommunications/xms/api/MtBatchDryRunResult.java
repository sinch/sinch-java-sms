package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchDryRunResultImpl.Builder.class)
public interface MtBatchDryRunResult {

	@Value.Immutable
	@JsonDeserialize(builder = PerRecipientImpl.Builder.class)
	public interface PerRecipient {

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
