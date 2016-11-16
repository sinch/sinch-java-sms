package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

public class JacksonUtilsTest {

	@Test
	public void byteArrayHexSerializerCanSerializeByteArray() throws Exception {
		ObjectMapper mapper =
		        serMapper(new JacksonUtils.ByteArrayHexSerializer());

		byte[] input = new byte[] { 0, 1, 2, 3, 4, 5 };

		String actual = mapper.writeValueAsString(input);

		assertThat(actual, is("\"000102030405\""));
	}

	@Test
	public void byteArrayHexDeserializerCanDeserializeHex() throws Exception {
		ObjectMapper mapper =
		        deserMapper(new JacksonUtils.ByteArrayHexDeserializer());

		String input = "\"000102030405\"";
		byte[] actual = mapper.readValue(input, byte[].class);

		assertThat(actual, is(new byte[] { 0, 1, 2, 3, 4, 5 }));
	}

	@Test(expected = InvalidFormatException.class)
	public void byteArrayHexDeserializerThrowsIfGivenInvalidHex()
	        throws Exception {
		ObjectMapper mapper =
		        deserMapper(new JacksonUtils.ByteArrayHexDeserializer());

		mapper.readValue("\"0001g2030405\"", byte[].class);
	}

	private static <T> ObjectMapper serMapper(
	        StdScalarSerializer<T> ser) {
		SimpleModule module = new SimpleModule()
		        .addSerializer(ser.handledType(), ser);
		return new ObjectMapper().registerModule(module);
	}

	@SuppressWarnings("unchecked")
	private static <T> ObjectMapper deserMapper(
	        StdScalarDeserializer<T> deser) {
		SimpleModule module = new SimpleModule()
		        .addDeserializer((Class<T>) deser.handledType(), deser);
		return new ObjectMapper().registerModule(module);
	}

}
