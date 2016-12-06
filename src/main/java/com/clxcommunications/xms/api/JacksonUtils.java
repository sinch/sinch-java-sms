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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.clxcommunications.xms.ClxApi;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * A collection of custom Jackson serializers and deserializers.
 */
class JacksonUtils {

	/**
	 * Jackson deserializer for hex encoded byte arrays.
	 */
	static final class ByteArrayHexDeserializer
	        extends FromStringDeserializer<byte[]> {

		private static final long serialVersionUID = 1L;

		public ByteArrayHexDeserializer() {
			super(byte[].class);
		}

		@Override
		protected byte[] _deserialize(String value, DeserializationContext ctxt)
		        throws IOException {
			try {
				return Hex.decodeHex(value.toCharArray());
			} catch (DecoderException e) {
				return (byte[]) ctxt.handleWeirdStringValue(handledType(),
				        value, e.getMessage());
			}
		}

	};

	/**
	 * Jackson serializer for hex encoded byte arrays.
	 */
	static final class ByteArrayHexSerializer
	        extends StdScalarSerializer<byte[]> {

		private static final long serialVersionUID = 1L;

		public ByteArrayHexSerializer() {
			super(byte[].class);
		}

		@Override
		public void serialize(byte[] value, JsonGenerator gen,
		        SerializerProvider provider) throws IOException {
			gen.writeString(Hex.encodeHexString(value));
		}

	};

	/**
	 * JSON deserializer of parameter values.
	 */
	static final class ParameterValuesDeserializer
	        extends StdNodeBasedDeserializer<ParameterValues> {

		private static final long serialVersionUID = 1L;

		public ParameterValuesDeserializer() {
			super(ParameterValues.class);
		}

		@Override
		public ParameterValues convert(JsonNode root,
		        DeserializationContext ctxt) throws IOException {
			ParameterValues.Builder builder = ClxApi.parameterValues();

			if (root.has("default")) {
				builder.defaultValue(root.get("default").asText());
			}

			Iterator<Entry<String, JsonNode>> it = root.fields();
			while (it.hasNext()) {
				Entry<String, JsonNode> entry = it.next();

				if ("default".equals(entry.getKey())) {
					builder.defaultValue(entry.getValue().asText());
				} else {
					builder.putSubstitution(entry.getKey(),
					        entry.getValue().asText());
				}
			}

			return builder.build();
		}

	}

	/**
	 * Jackson serializer of parameter values.
	 */
	static final class ParameterValuesSerializer
	        extends StdSerializer<ParameterValues> {

		private static final long serialVersionUID = 1L;

		public ParameterValuesSerializer() {
			super(ParameterValues.class);
		}

		@Override
		public void serialize(ParameterValues value, JsonGenerator gen,
		        SerializerProvider provider) throws IOException {
			gen.writeStartObject();

			for (Entry<String, String> entry : value.substitutions()
			        .entrySet()) {
				gen.writeStringField(entry.getKey(), entry.getValue());
			}

			if (value.defaultValue() != null) {
				gen.writeStringField("default", value.defaultValue());
			}

			gen.writeEndObject();
		}

	}

}
