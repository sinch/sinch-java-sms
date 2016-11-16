package com.clxcommunications.xms.api;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.UpdateValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePublic
@JsonSerialize(as = ImmutableMtBatchTextSmsUpdate.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsUpdate extends MtBatchSmsUpdate {

	public static class Builder extends ImmutableMtBatchTextSmsUpdate.Builder {

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

		public Builder unsetTags() {
			return this.tags(UpdateValue.<Set<String>> unset());
		}

		public Builder unsetParameters() {
			return this.parameters(
			        UpdateValue.<Map<String, ParameterValues>> unset());
		}

	}

	public static final Builder builder() {
		return new Builder();
	}

	@Nullable
	public abstract String body();

	@Nullable
	public abstract UpdateValue<Set<String>> tags();

	@Nullable
	public abstract UpdateValue<Map<String, ParameterValues>> parameters();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
