package com.clxcommunications.xms.api;

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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Describes the different types of delivery reports supported by XMS.
 */
@Value.Immutable(builder = false, copy = false, intern = true)
@ValueStylePackageDirect
public abstract class ReportType {

	/**
	 * No delivery report is desired or available.
	 */
	public static final ReportType NONE =
	        ReportTypeImpl.of("none");

	/**
	 * A summary delivery report is desired or available.
	 */
	public static final ReportType SUMMARY =
	        ReportTypeImpl.of("summary");

	/**
	 * A full delivery report is desired or available.
	 */
	public static final ReportType FULL =
	        ReportTypeImpl.of("full");

	/**
	 * A per recipient delivery report is desired or available.
	 */
	public static final ReportType PER_RECIPIENT =
	        ReportTypeImpl.of("per_recipient");

	@JsonValue
	public abstract String type();

	@JsonCreator
	public static ReportType of(String type) {
		return ReportTypeImpl.of(type);
	}

}
