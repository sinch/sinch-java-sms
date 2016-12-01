package com.clxcommunications.xms.api;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.OffsetDateTime;

import com.clxcommunications.xms.UpdateValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@ValueStylePackage
@JsonSerialize(as = MtBatchTextSmsUpdateImpl.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsUpdate extends MtBatchSmsUpdate {

	public static class Builder extends MtBatchTextSmsUpdateImpl.Builder {

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

		public Builder unsetParameters() {
			return this.parameters(
			        UpdateValue.<Map<String, ParameterValues>> unset());
		}

		public Builder parameters(Map<String, ParameterValues> params) {
			if (params == null) {
				return this.unsetParameters();
			} else {
				return this.parameters(UpdateValue.set(params));
			}
		}

	}

	@Nullable
	public abstract String body();

	@Nullable
	public abstract UpdateValue<Map<String, ParameterValues>> parameters();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
