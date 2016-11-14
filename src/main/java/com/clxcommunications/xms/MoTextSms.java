package com.clxcommunications.xms;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A mobile originated (MO) message with textual content.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = ImmutableMoTextSms.Builder.class)
@JsonTypeName("mo_text")
public abstract class MoTextSms extends MoSms {

	/**
	 * The textual message body.
	 * 
	 * @return the message body
	 */
	public abstract String body();

	/**
	 * The keyword provided with this MO message, if available.
	 * 
	 * @return a keyword if available, otherwise <code>null</code>
	 */
	@Nullable
	public abstract String keyword();

}
