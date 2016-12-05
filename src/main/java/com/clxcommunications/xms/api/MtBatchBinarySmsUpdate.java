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

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.UpdateValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonSerialize(as = MtBatchBinarySmsUpdateImpl.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsUpdate extends MtBatchSmsUpdate {

	public static class Builder extends MtBatchBinarySmsUpdateImpl.Builder {

		public Builder unsetDeliveryReport() {
			return this.deliveryReport(UpdateValue.<ReportType> unset());
		}

		public Builder deliveryReport(ReportType deliveryReport) {
			if (deliveryReport == null) {
				return this.unsetDeliveryReport();
			} else {
				return this.deliveryReport(UpdateValue.set(deliveryReport));
			}
		}

		public Builder unsetSendAt() {
			return this.sendAt(UpdateValue.<OffsetDateTime> unset());
		}

		public Builder sendAt(OffsetDateTime time) {
			if (time == null) {
				return this.unsetSendAt();
			} else {
				return this.sendAt(UpdateValue.set(time));
			}
		}

		public Builder unsetExpireAt() {
			return this.expireAt(UpdateValue.<OffsetDateTime> unset());
		}

		public Builder expireAt(OffsetDateTime time) {
			if (time == null) {
				return this.unsetExpireAt();
			} else {
				return this.expireAt(UpdateValue.set(time));
			}
		}

		public Builder unsetCallbackUrl() {
			return this.callbackUrl(UpdateValue.<URI> unset());
		}

		public Builder callbackUrl(URI url) {
			if (url == null) {
				return this.unsetCallbackUrl();
			} else {
				return this.callbackUrl(UpdateValue.set(url));
			}
		}

	}

	@Nullable
	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	@Nullable
	public abstract byte[] body();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
