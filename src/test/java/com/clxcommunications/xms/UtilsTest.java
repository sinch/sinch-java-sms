package com.clxcommunications.xms;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void canJoinEmpty() {
		assertThat(Utils.join("sep"), isEmptyString());
	}

	@Test
	public void canJoinSingleton() {
		assertThat(Utils.join("sep", "foo"), is("foo"));
	}

	@Test
	public void canJoinMultiple() {
		assertThat(Utils.join("sep", "foo", "bar"), is("foosepbar"));
	}

	@Test(expected = NullPointerException.class)
	public void joinThrowsOnNullDelimiter() {
		Utils.join(null, "foo");
	}

	@Test(expected = NullPointerException.class)
	public void joinThrowsOnNullArray() {
		Utils.join("sep", (String[]) null);
	}

	@Test(expected = NullPointerException.class)
	public void joinThrowsOnNullCollection() {
		Utils.join("sep", (List<String>) null);
	}

	@Test
	public void requireNonNullIsIdOnNonNull() throws Exception {
		Object o = new Object();

		assertThat(Utils.requireNonNull(o, "xyz"), is(sameInstance(o)));
	}

	@Test(expected = NullPointerException.class)
	public void requireNonNullThrowsNpeOnNull() throws Exception {
		try {
			Utils.requireNonNull(null, "abcde");
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is("abcde"));
			throw e;
		}
	}

}
