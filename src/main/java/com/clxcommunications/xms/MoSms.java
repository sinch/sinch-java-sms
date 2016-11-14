package com.clxcommunications.xms;

import javax.annotation.Nullable;

import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(MoTextSms.class),
        @Type(MoBinarySms.class)
})
public abstract class MoSms {

	MoSms() {
		// Intentionally left empty.
	}

	/**
	 * The unique message identifier.
	 * 
	 * @return a message identifier
	 */
	public abstract String id();

	/**
	 * The originating MSISDN.
	 * 
	 * @return an originating address
	 */
	public abstract String from();

	public abstract String to();

	/**
	 * The MCCMNC of the originating operator, if available.
	 * 
	 * @return an MCCMNC
	 */
	@Nullable
	public abstract String operator();

	@JsonProperty("sent_at")
	@Nullable
	public abstract OffsetDateTime sentAt();

	@JsonProperty("received_at")
	public abstract OffsetDateTime receivedAt();

}
