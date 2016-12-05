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

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;

import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(MtBatchTextSmsResult.class),
        @Type(MtBatchBinarySmsResult.class)
})
public abstract class MtBatchSmsResult {

	MtBatchSmsResult() {
		// Intentionally left empty.
	}

	public abstract BatchId id();

	public abstract List<String> to();

	@Nullable
	public abstract String from();

	@Nullable
	public abstract ReportType deliveryReport();

	@Nullable
	public abstract URI callbackUrl();

	@Nullable
	@JsonProperty("send_at")
	public abstract OffsetDateTime sendAt();

	@Nullable
	@JsonProperty("expire_at")
	public abstract OffsetDateTime expireAt();

	@Nullable
	@JsonProperty("created_at")
	public abstract OffsetDateTime createdAt();

	@Nullable
	@JsonProperty("modified_at")
	public abstract OffsetDateTime modifiedAt();

	public abstract boolean canceled();

	@JsonIgnore
	public final boolean isTextBatch() {
		return this instanceof MtBatchTextSmsResult;
	}

	@JsonIgnore
	public final boolean isBinaryBatch() {
		return this instanceof MtBatchBinarySmsResult;
	}

	@JsonIgnore
	public final MtBatchTextSmsResult asTextBatch() {
		return (MtBatchTextSmsResult) this;
	}

	@JsonIgnore
	public final MtBatchBinarySmsResult asBinaryBatch() {
		return (MtBatchBinarySmsResult) this;
	}

}
