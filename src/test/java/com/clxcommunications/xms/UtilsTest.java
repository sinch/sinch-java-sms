package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void canJoinEmpty() {
		assertThat(Utils.join("sep"), is(""));
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
