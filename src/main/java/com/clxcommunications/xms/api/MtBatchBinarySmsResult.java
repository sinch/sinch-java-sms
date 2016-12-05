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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Objects of this class contain information about a binary SMS batch. The
 * information includes the batch identifier, the creation time, and so on.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = MtBatchBinarySmsResultImpl.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsResult extends MtBatchSmsResult {

	/**
	 * Builder of binary batch results.
	 */
	public static class Builder extends MtBatchBinarySmsResultImpl.Builder {

	}

	/**
	 * The User Data Header of the message.
	 * 
	 * @return a byte array containing the UDH
	 */
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
