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

/**
 * A description of updates that can be applied to a binary SMS batch message.
 */
@Value.Immutable
@ValueStylePackage
@JsonSerialize(as = MtBatchBinarySmsUpdateImpl.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsUpdate extends MtBatchSmsUpdate {

	/**
	 * A builder of binary batch message updates.
	 */
	public static class Builder extends MtBatchBinarySmsUpdateImpl.Builder {

		/**
		 * Resets the delivery report type to the messaging system default
		 * value.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetDeliveryReport() {
			return this.deliveryReport(UpdateValue.<ReportType> unset());
		}

		/**
		 * Updates the delivery report type of this batch. If given a
		 * <code>null</code> reference then this is equivalent to calling
		 * {@link #unsetDeliveryReport()}.
		 * 
		 * @param deliveryReport
		 *            the new delivery report type or <code>null</code> to unset
		 * @return this builder for use in a chained invocation
		 */
		public Builder deliveryReport(ReportType deliveryReport) {
			if (deliveryReport == null) {
				return this.unsetDeliveryReport();
			} else {
				return this.deliveryReport(UpdateValue.set(deliveryReport));
			}
		}

		/**
		 * Unsets the scheduled send time. This has the effect of immediately
		 * starting to send the batch.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetSendAt() {
			return this.sendAt(UpdateValue.<OffsetDateTime> unset());
		}

		/**
		 * Updates the scheduled send time. If given a <code>null</code>
		 * reference then this is equivalent to calling {@link #unsetSendAt()}.
		 * 
		 * @param time
		 *            the new scheduled send time
		 * @return this builder for use in a chained invocation
		 */
		public Builder sendAt(OffsetDateTime time) {
			if (time == null) {
				return this.unsetSendAt();
			} else {
				return this.sendAt(UpdateValue.set(time));
			}
		}

		/**
		 * Resets the batch expire time to the messaging system default value.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetExpireAt() {
			return this.expireAt(UpdateValue.<OffsetDateTime> unset());
		}

		/**
		 * Updates the batch expire time. If given a <code>null</code> reference
		 * then this is equivalent to calling {@link #unsetExpireAt()}.
		 * 
		 * @param time
		 *            the new expire time
		 * @return this builder for use in a chained invocation
		 */
		public Builder expireAt(OffsetDateTime time) {
			if (time == null) {
				return this.unsetExpireAt();
			} else {
				return this.expireAt(UpdateValue.set(time));
			}
		}

		/**
		 * Resets the callback URL to the default callback URL.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder unsetCallbackUrl() {
			return this.callbackUrl(UpdateValue.<URI> unset());
		}

		/**
		 * Updates the callback URL. If given a <code>null</code> reference then
		 * this is equivalent to calling {@link #unsetCallbackUrl()}.
		 * 
		 * @param url
		 *            the new callback URL
		 * @return this builder for use in a chained invocation
		 */
		public Builder callbackUrl(URI url) {
			if (url == null) {
				return this.unsetCallbackUrl();
			} else {
				return this.callbackUrl(UpdateValue.set(url));
			}
		}

	}

	/**
	 * The updated User Data Header of the message.
	 * 
	 * @return a byte array containing the UDH; <code>null</code> if unchanged
	 */
	@Nullable
	@JsonSerialize(using = JacksonUtils.ByteArrayHexSerializer.class)
	@JsonDeserialize(using = JacksonUtils.ByteArrayHexDeserializer.class)
	public abstract byte[] udh();

	/**
	 * The updated binary message body.
	 * 
	 * @return a byte array containing the body; <code>null</code> if unchanged
	 */
	@Nullable
	public abstract byte[] body();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
