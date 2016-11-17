package com.clxcommunications.xms.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Describes auto updates of groups. Auto updates allow end users to add or
 * remove themselves from groups.
 */
@Value.Immutable
@ValueStylePublic
@JsonDeserialize(builder = AutoUpdateImpl.Builder.class)
public interface AutoUpdate {

	/**
	 * An MSISDN or short code. A mobile originated message sent to this address
	 * can trigger the auto update if a matching keyword is found.
	 * 
	 * @return a non-null MSISDN or short code
	 */
	String to();

	@JsonProperty("add_keyword_first")
	@Nullable
	String addKeywordFirst();

	@JsonProperty("add_keyword_second")
	@Nullable
	String addKeywordSecond();

	@JsonProperty("remove_keyword_first")
	@Nullable
	String removeKeywordFirst();

	@JsonProperty("remove_keyword_second")
	@Nullable
	String removeKeywordSecond();

}
