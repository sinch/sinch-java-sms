/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sinch.xms.api;

import java.util.Map;

import javax.annotation.Nonnull;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Objects of this class contain information about a textual SMS batch. The
 * information includes the text message body, the batch identifier, the
 * creation time, and so on.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchTextSmsResult.Builder.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsResult extends MtBatchSmsResult {

	/**
	 * Builder of textual batch results.
	 */
	public static class Builder extends MtBatchTextSmsResultImpl.Builder {

		Builder() {
		}

	}

	/**
	 * Creates a builder of {@link MtBatchTextSmsResult} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final MtBatchTextSmsResult.Builder builder() {
		return new Builder();
	}

	/**
	 * The message text or template. If this describes a template then
	 * {@link #parameters()} describes the parameter substitutions.
	 * <p>
	 * See {@link MtBatchTextSmsCreate#body()} for a more thorough description
	 * of this field.
	 * 
	 * @return the message to send
	 */
	public abstract String body();

	/**
	 * The message template parameter substitutions. If {@link #body()}
	 * describes a template then this returns the substitutions for the template
	 * parameters.
	 * 
	 * @return a map from template variable to parameter values
	 * @see #body()
	 */
	@JsonInclude(Include.NON_EMPTY)
	public abstract Map<String, ParameterValues> parameters();

}
