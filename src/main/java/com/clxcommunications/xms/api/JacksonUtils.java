package com.clxcommunications.xms.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonUtils {

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

	static final class BatchIdDeserializer
	        extends FromStringDeserializer<BatchId> {

		private static final long serialVersionUID = 1L;

		public BatchIdDeserializer() {
			super(BatchId.class);
		}

		@Override
		protected BatchId _deserialize(String value,
		        DeserializationContext ctxt) throws IOException {
			return BatchId.of(value);
		}

	};

	static final class BatchIdSerializer extends StdScalarSerializer<BatchId> {

		private static final long serialVersionUID = 1L;

		public BatchIdSerializer() {
			super(BatchId.class);
		}

		@Override
		public void serialize(BatchId value, JsonGenerator gen,
		        SerializerProvider provider) throws IOException {
			gen.writeString(value.id());
		}

	};

	static final class DeliveryReportDeserializer
	        extends FromStringDeserializer<DeliveryReport> {

		private static final long serialVersionUID = 1L;

		public DeliveryReportDeserializer() {
			super(DeliveryReport.class);
		}

		@Override
		protected DeliveryReport _deserialize(String value,
		        DeserializationContext ctxt) throws IOException {
			return ImmutableDeliveryReport.of(value);
		}

	};

	static final class DeliveryReportSerializer
	        extends StdScalarSerializer<DeliveryReport> {

		private static final long serialVersionUID = 1L;

		public DeliveryReportSerializer() {
			super(DeliveryReport.class);
		}

		@Override
		public void serialize(DeliveryReport value, JsonGenerator gen,
		        SerializerProvider provider) throws IOException {
			gen.writeString(value.style());
		}

	};

	/**
	 * JSON deserializer of parameter values.
	 */
	public static final class ParameterValuesDeserializer
	        extends StdNodeBasedDeserializer<ParameterValues> {

		private static final long serialVersionUID = 1L;

		public ParameterValuesDeserializer() {
			super(ParameterValues.class);
		}

		@Override
		public ParameterValues convert(JsonNode root,
		        DeserializationContext ctxt) throws IOException {
			ImmutableParameterValues.Builder builder =
			        ImmutableParameterValues.builder();

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

	public static final class ParameterValuesSerializer
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
