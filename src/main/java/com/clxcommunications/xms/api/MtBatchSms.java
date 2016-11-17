package com.clxcommunications.xms.api;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
	public abstract ReportType deliveryReport();

	@Nullable
	@JsonProperty("send_at")
	public abstract OffsetDateTime sendAt();

	@Nullable
	@JsonProperty("expire_at")
	public abstract OffsetDateTime expireAt();

	@Nullable
	@JsonProperty("callback_url")
	public abstract URI callbackUrl();

	/**
	 * The tags that should be attached to this message.
	 * 
	 * @return a non-null set of tags
	 */
	@JsonInclude(Include.NON_EMPTY)
	public abstract Set<String> tags();

	@OverridingMethodsMustInvokeSuper
	protected void check() {
		if (to().isEmpty()) {
			throw new IllegalStateException("no destination");
		}
	}

}
