package com.clxcommunications.xms.api;

/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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
