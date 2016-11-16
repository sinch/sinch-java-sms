package com.clxcommunications.xms.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.clxcommunications.xms.ValueStylePackage;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = ImmutableMoBinarySms.class)
@JsonTypeName("mo_binary")
public abstract class MoBinarySms extends MoSms {

	@Nullable
	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	public abstract byte[] body();

}
