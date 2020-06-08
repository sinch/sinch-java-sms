/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sinch.xms.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A binary mobile originated SMS message.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MoBinarySmsImpl.class)
@JsonTypeName("mo_binary")
public abstract class MoBinarySms extends MoSms {

	/**
	 * A builder of binary MO messages.
	 */
	public static final class Builder extends MoBinarySmsImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link MoBinarySms} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final MoBinarySms.Builder builder() {
		return new Builder();
	}

	/**
	 * The User Data Header of the message.
	 * 
	 * @return a byte array containing the UDH
	 */
	@Nullable
	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	/**
	 * The binary message body.
	 * 
	 * @return a byte array containing the body
	 */
	public abstract byte[] body();

}
