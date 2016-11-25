package com.clxcommunications.xms.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Describes auto updates of groups. Auto updates allow end users to add or
 * remove themselves from groups.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = AutoUpdate.Builder.class)
public abstract class AutoUpdate {

	/**
	 * A builder of auto update descriptions. Initialize the desired attributes
	 * and call the build method to instantiate the object.
	 */
	@NotThreadSafe
	public static class Builder extends AutoUpdateImpl.Builder {

		/**
		 * The keyword trigger to use for adding a number to a group.
		 * 
		 * @param firstWord
		 *            the first keyword, can be <code>null</code>
		 * @param secondWord
		 *            the second keyword, can be <code>null</code>
		 * @return this builder for use in a chained invocation
		 */
		public Builder add(String firstWord, String secondWord) {
			return this.add(KeywordPair.of(firstWord, secondWord));
		}

		/**
		 * The keyword trigger to use for removing a number to a group.
		 * 
		 * @param firstWord
		 *            the first keyword, can be <code>null</code>
		 * @param secondWord
		 *            the second keyword, can be <code>null</code>
		 * @return this builder for use in a chained invocation
		 */
		public Builder remove(String firstWord, String secondWord) {
			return this.remove(KeywordPair.of(firstWord, secondWord));
		}

	}

	/**
	 * Instantiates a fresh builder of auto update objects.
	 * 
	 * @return a fresh auto update builder
	 */
	@Nonnull
	public static final Builder builder() {
		return new AutoUpdate.Builder();
	}

	/**
	 * An MSISDN or short code. A mobile originated message sent to this address
	 * can trigger the auto update if a matching keyword is found.
	 * 
	 * @return a non-null MSISDN or short code
	 */
	public abstract String to();

	/**
	 * The keyword trigger used to add a member.
	 * 
	 * @return a keyword pair
	 */
	@Nullable
	public abstract KeywordPair add();

	/**
	 * The keyword trigger used to remove a member.
	 * 
	 * @return a keyword pair
	 */
	@Nullable
	public abstract KeywordPair remove();

}
