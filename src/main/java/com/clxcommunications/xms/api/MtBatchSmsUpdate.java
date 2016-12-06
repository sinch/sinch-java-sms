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
package com.clxcommunications.xms.api;

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.UpdateValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Objects of this type can be used to update previously submitted MT batches.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(MtBatchTextSmsUpdate.class),
        @Type(MtBatchBinarySmsUpdate.class)
})
public abstract class MtBatchSmsUpdate {

	/**
	 * The message destinations to add to the batch.
	 * 
	 * @return a list of MSISDNs or group IDs
	 */
	@Nullable
	@JsonProperty("to_add")
	public abstract List<String> toAdd();

	/**
	 * The message destinations to remove from the batch.
	 * 
	 * @return a list of MSISDNs or group IDs
	 */
	@Nullable
	@JsonProperty("to_remove")
	public abstract List<String> toRemove();

	/**
	 * The message originator.
	 * 
	 * @return an MSISDN or short code
	 */
	@Nullable
	public abstract String from();

	/**
	 * Description of how to update the batch delivery report value.
	 * 
	 * @return an update description
	 * @see MtBatchSmsCreate#deliveryReport()
	 */
	@Nullable
	@JsonProperty("delivery_report")
	public abstract UpdateValue<ReportType> deliveryReport();

	/**
	 * Description of how to update the batch send at value.
	 * 
	 * @return an update description
	 * @see MtBatchSmsCreate#sendAt()
	 */
	@Nullable
	@JsonProperty("send_at")
	public abstract UpdateValue<OffsetDateTime> sendAt();

	/**
	 * Description of how to update the batch expire at value.
	 * 
	 * @return an update description
	 * @see MtBatchSmsCreate#expireAt()
	 */
	@Nullable
	@JsonProperty("expire_at")
	public abstract UpdateValue<OffsetDateTime> expireAt();

	/**
	 * Description of how to update the batch callback URL.
	 * 
	 * @return an update description
	 * @see MtBatchSmsCreate#callbackUrl()
	 */
	@Nullable
	@JsonProperty("callback_url")
	public abstract UpdateValue<URI> callbackUrl();

	@OverridingMethodsMustInvokeSuper
	protected void check() {
		if (from() != null && from().isEmpty()) {
			throw new IllegalStateException("empty from address");
		}
	}

}
