package com.clxcommunications.xms.api;

import java.net.URI;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.UpdateValue;
import com.clxcommunications.xms.ValueStylePublic;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePublic
@JsonSerialize(as = ImmutableMtBatchBinarySmsUpdate.class)
@JsonTypeName("mt_binary")
public abstract class MtBatchBinarySmsUpdate extends MtBatchSmsUpdate {

	public static class Builder
	        extends ImmutableMtBatchBinarySmsUpdate.Builder {

		public Builder unsetDeliveryReport() {
			return this.deliveryReport(UpdateValue.<DeliveryReport> unset());
		}

		public Builder unsetSendAt() {
			return this.sendAt(UpdateValue.<OffsetDateTime> unset());
		}

		public Builder unsetExpireAt() {
			return this.expireAt(UpdateValue.<OffsetDateTime> unset());
		}

		public Builder unsetCallbackUrl() {
			return this.callbackUrl(UpdateValue.<URI> unset());
		}

	}

	public static final Builder builder() {
		return new Builder();
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
