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

	public MtBatchSmsUpdate() {
		super();
	}

	/**
	 * The message destinations.
	 * 
	 * @return a list of MSISDNs or group IDs
	 */
	@Nullable
	public abstract List<String> to();

	/**
	 * The message originator.
	 * 
	 * @return an MSISDN or short code
	 */
	@Nullable
	public abstract String from();

	@Nullable
	@JsonProperty("delivery_report")
	public abstract UpdateValue<DeliveryReport> deliveryReport();

	@Nullable
	@JsonProperty("send_at")
	public abstract UpdateValue<OffsetDateTime> sendAt();

	@Nullable
	@JsonProperty("expire_at")
	public abstract UpdateValue<OffsetDateTime> expireAt();

	@Nullable
	@JsonProperty("callback_url")
	public abstract UpdateValue<URI> callbackUrl();

	@OverridingMethodsMustInvokeSuper
	protected void check() {
		if (to() != null && to().isEmpty()) {
			throw new IllegalStateException("no destination");
		}
	}

}
