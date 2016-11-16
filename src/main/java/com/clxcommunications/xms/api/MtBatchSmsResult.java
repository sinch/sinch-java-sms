package com.clxcommunications.xms.api;

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;

import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(MtBatchTextSmsResult.class),
        @Type(MtBatchBinarySmsResult.class)
})
public abstract class MtBatchSmsResult {

	MtBatchSmsResult() {
		// Intentionally left empty.
	}

	public abstract BatchId id();

	public abstract List<String> to();

	@Nullable
	public abstract String from();

	@Nullable
	public abstract DeliveryReport deliveryReport();

	@Nullable
	public abstract URI callbackUrl();

	@Nullable
	@JsonProperty("send_at")
	public abstract OffsetDateTime sendAt();

	@Nullable
	@JsonProperty("expire_at")
	public abstract OffsetDateTime expireAt();

	@Nullable
	@JsonProperty("created_at")
	public abstract OffsetDateTime createdAt();

	@Nullable
	@JsonProperty("modified_at")
	public abstract OffsetDateTime modifiedAt();

	public abstract boolean canceled();

	@JsonIgnore
	public final boolean isTextBatch() {
		return this instanceof MtBatchTextSmsResult;
	}

	@JsonIgnore
	public final boolean isBinaryBatch() {
		return this instanceof MtBatchBinarySmsResult;
	}

	@JsonIgnore
	public final MtBatchTextSmsResult asTextBatch() {
		return (MtBatchTextSmsResult) this;
	}

	@JsonIgnore
	public final MtBatchBinarySmsResult asBinaryBatch() {
		return (MtBatchBinarySmsResult) this;
	}

}
