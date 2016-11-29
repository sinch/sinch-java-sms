package com.clxcommunications.xms.api;

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
