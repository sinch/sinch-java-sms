package com.clxcommunications.xms.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A pair containing the first and second word of a group auto update trigger.
 * Either trigger word may be <code>null</code>.
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = KeywordPairImpl.class)
public abstract class KeywordPair {

	@Nullable
	@JsonProperty("first_word")
	public abstract String firstWord();

	@Nullable
	@JsonProperty("second_word")
	public abstract String secondWord();

	@Nonnull
	public static KeywordPair of(@Nullable String firstWord,
	        @Nullable String secondWord) {
		return KeywordPairImpl.of(firstWord, secondWord);
	}

}
