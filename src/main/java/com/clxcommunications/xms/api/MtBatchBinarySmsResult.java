package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(as = MtBatchBinarySmsResultImpl.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsResult extends MtBatchSmsResult {

	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	public abstract byte[] body();

}
