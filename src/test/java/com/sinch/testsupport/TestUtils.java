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
package com.sinch.testsupport;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.hamcrest.Matcher;

import com.sinch.xms.api.BatchId;
import com.sinch.xms.api.GroupId;

/**
 * A few utils that are handy to have around in the test suite.
 */
public final class TestUtils {

	public static final Charset US_ASCII = Charset.forName("US-ASCII");

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Field used by, e.g., {@link #freshServicePlanId()} and
	 * {@link #freshBatchId()} to generate unique values.
	 */
	private static final AtomicInteger uniqueCounter = new AtomicInteger();

	/**
	 * Creates and returns a fresh unique SMS identifier.
	 * 
	 * @return a unique, non-null, message identifier
	 */
	public static String freshSmsId() {
		return "sms" + uniqueCounter.incrementAndGet();
	}

	/**
	 * Creates and returns a fresh unique service plan ID.
	 * 
	 * @return a unique, non-null, service plan ID
	 */
	public static String freshServicePlanId() {
		return "user" + uniqueCounter.incrementAndGet();
	}

	/**
	 * Creates and returns a fresh unique authentication token.
	 * 
	 * @return a unique authentication token.
	 */
	@Nonnull
	public static String freshToken() {
		return "token" + uniqueCounter.incrementAndGet();
	}

	/**
	 * Creates and returns a fresh batch ID. Guaranteed unique for this run.
	 * 
	 * @return a unique, non-null, batch ID.
	 */
	public static BatchId freshBatchId() {
		return BatchId.of("batch" + uniqueCounter.incrementAndGet());
	}

	/**
	 * Creates and returns a fresh group ID. Guaranteed unique for this run.
	 * 
	 * @return a unique, non-null, group ID.
	 */
	public static GroupId freshGroupId() {
		return GroupId.of("group" + uniqueCounter.incrementAndGet());
	}

	/**
	 * Hamcrest matcher that verifies that two strings describe the same JSON
	 * content.
	 * 
	 * @param expected
	 *            a JSON encoded string
	 * @return a Hamcrest matcher
	 */
	@Nonnull
	public static Matcher<String> jsonEqualTo(final String expected) {
		return new JsonEqualTo(expected);
	}

}
