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
package com.sinch.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class ByteInOutStreamTest {

	@Property
	public void canWriteByteBufferWithArray(byte[] bytes) throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(bytes.length + 10);

		doWriteByteBufferTest(buf, bytes);
	}

	@Property
	public void canWriteByteBufferWithNoArray(byte[] bytes) throws Exception {
		ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length + 10);

		doWriteByteBufferTest(buf, bytes);
	}

	@Property
	public void canProduceGoodInputStream(byte[] bytes) throws Exception {
		ByteInOutStream bios = new ByteInOutStream(10);

		bios.write(bytes);

		InputStream is = bios.toInputStream();

		byte[] readBuf = new byte[bytes.length + 10];
		int read = is.read(readBuf);
		byte[] actual = Arrays.copyOf(readBuf, bytes.length);

		/*
		 * Read is -1 if the stream was empty, should only happen if the input
		 * byte array was empty.
		 */
		if (read == -1) {
			assertThat(0, is(bytes.length));
		} else {
			assertThat(read, is(bytes.length));
		}

		assertThat(actual, is(bytes));

		is.close();
		bios.close();
	}

	private void doWriteByteBufferTest(ByteBuffer buf, byte[] bytes)
	        throws IOException {
		buf.put(bytes);
		buf.flip();

		ByteInOutStream bios = new ByteInOutStream(10);

		bios.write(buf);

		byte[] actual = bios.toByteArray();

		assertThat(actual, is(bytes));

		bios.close();
	}

}
