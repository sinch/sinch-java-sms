package com.clxcommunications.xms.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MoBinarySmsImpl.class)
@JsonTypeName("mo_binary")
public abstract class MoBinarySms extends MoSms {

	public static final class Builder extends MoBinarySmsImpl.Builder {

	}

	@Nullable
	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	public abstract byte[] body();

}
