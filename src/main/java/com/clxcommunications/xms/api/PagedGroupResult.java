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

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A page within a paged group listing.
 */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = PagedGroupResult.Builder.class)
public abstract class PagedGroupResult extends Page<GroupResult> {

	/**
	 * A builder of group result pages.
	 */
	public static class Builder extends PagedGroupResultImpl.Builder {

	}

	@JsonProperty("groups")
	@Override
	public abstract List<GroupResult> content();

}
