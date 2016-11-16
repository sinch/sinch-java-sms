package com.clxcommunications.xms.api;

import org.immutables.value.Value;

import com.clxcommunications.xms.ClxApi;
import com.clxcommunications.xms.ValueStylePublic;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePublic
@JsonDeserialize(as = ImmutableMtBatchBinarySms.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySms extends MtBatchSms {

	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	public abstract byte[] body();

	@Override
	@Value.Check
	protected void check() {
		super.check();

		if (udh().length + body().length > ClxApi.MAX_BODY_BYTES) {
			throw new IllegalStateException("udh and body too long");
		}
	}

}
