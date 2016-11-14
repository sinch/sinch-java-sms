package com.clxcommunications.xms;

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(MtBatchTextSms.class),
        @Type(MtBatchBinarySms.class)
})
public abstract class MtBatchSms {

	public MtBatchSms() {
		super();
	}

	public abstract List<String> to();

	public abstract String from();

	@Nullable
	@JsonProperty("campaign_id")
	public abstract String campaignId();

	@Nullable
	@JsonProperty("delivery_report")
	public abstract DeliveryReport deliveryReport();

	@Nullable
	@JsonProperty("send_at")
	public abstract OffsetDateTime sendAt();

	@Nullable
	@JsonProperty("expire_at")
	public abstract OffsetDateTime expireAt();

	@Nullable
	@JsonProperty("callback_url")
	public abstract URI callbackUrl();

	@OverridingMethodsMustInvokeSuper
	protected void check() {
		if (to().isEmpty()) {
			throw new IllegalStateException("no destination");
		}
	}

}
