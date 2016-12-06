/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
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
package com.clxcommunications.xms.api;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Container of all necessary parameters to create a text SMS batch message.
 * <p>
 * A minimal definition has defined values for
 * <ul>
 * <li>{@link #to()},</li>
 * <li>{@link #from()},</li>
 * <li>{@link #body()}.</li>
 * </ul>
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchTextSmsCreate.Builder.class)
@JsonTypeName("mt_text")
public abstract class MtBatchTextSmsCreate extends MtBatchSmsCreate {

	/**
	 * A builder of textual batch messages.
	 */
	public static class Builder extends MtBatchTextSmsCreateImpl.Builder {

	}

	/**
	 * The message text or template. If this describes a template then
	 * {@link #parameters()} must describe the parameter substitutions.
	 * <p>
	 * The typical way to use templates is
	 * 
	 * <pre>
	 * ClxApi.batchTextSms()
	 *     .from("12345")
	 *     .addTo("987654321")
	 *     // Other initialization
	 *     .body("Hello, ${name}")
	 *     .putParameter("name",
	 *         ClxApi.parameterValues()
	 *             .putSubstitution("987654321", "Jane")
	 *             .default("valued customer")
	 *             .build())
	 *     .build();
	 * </pre>
	 * 
	 * @return the message to send
	 */
	public abstract String body();

	/**
	 * The message template parameter substitutions. If {@link #body()}
	 * describes a template then this must return the necessary substitutions
	 * for all template parameters.
	 * 
	 * @return a map from template variable to parameter values
	 * @see #body()
	 */
	@JsonInclude(Include.NON_EMPTY)
	public abstract Map<String, ParameterValues> parameters();

	@Override
	@Value.Check
	protected void check() {
		super.check();
	}

}
