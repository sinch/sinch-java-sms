package com.clxcommunications.xms.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Describes auto updates of groups.
 */
@Value.Immutable
@ValueStylePublic
@JsonDeserialize(builder = AutoUpdateImpl.Builder.class)
public interface AutoUpdate {

	/**
	 * An MSISDN or short code
	 * 
	 * @return a non-null address
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
