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
package com.clxcommunications.xms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An output stream that reads and buffers bytes. It is capable of later
 * creating an input stream for the read bytes without copying the bytes again.
 * <p>
 * Internally this relies on {@link ByteArrayOutputStream} and
 * {@link ByteArrayInputStream}, which unfortunately are riddled with
 * <code>synchronized</code> keywords possibly making performance suffer.
 */
@ParametersAreNonnullByDefault
final class ByteInOutStream extends ByteArrayOutputStream {

	public ByteInOutStream(int initialSize) {
		super(initialSize);
	}

	/**
	 * Writes the given byte buffer to this stream.
	 * 
	 * @param buf
	 *            the byte buffer to write
	 */
	public void write(ByteBuffer buf) {
		/*
		 * The HTTP client library currently always uses a backing array but in
		 * case this changes in the future we also support byte buffers without
		 * backing array.
		 */
		if (buf.hasArray()) {
			write(buf.array(),
			        buf.arrayOffset() + buf.position(),
			        buf.remaining());
		} else {
			for (int i = buf.position(); i < buf.limit(); i++) {
				write(buf.get(i));
			}
		}
	}

	/**
	 * Creates an input stream from which the written data can be read. Note,
	 * the returned input stream shares its data buffer with this class. It is
	 * therefore important to not interact further with this object after this
	 * method is called!
	 * 
	 * @return an input stream
	 */
	@Nonnull
	public InputStream toInputStream() {
		return new ByteArrayInputStream(buf, 0, count);
	}

}
