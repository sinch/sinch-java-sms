package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MtBatchBinarySmsCreateImpl.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsCreate extends MtBatchSmsCreate {

	public static class Builder extends MtBatchBinarySmsCreateImpl.Builder {

	}

	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	public abstract byte[] body();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
