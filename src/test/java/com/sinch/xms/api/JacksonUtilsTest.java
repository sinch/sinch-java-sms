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
package com.sinch.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.junit.Test;

public class JacksonUtilsTest {

  @Test
  public void byteArrayHexSerializerCanSerializeByteArray() throws Exception {
    ObjectMapper mapper = serMapper(new JacksonUtils.ByteArrayHexSerializer());

    byte[] input = new byte[] {0, 1, 2, 3, 4, 5};

    String actual = mapper.writeValueAsString(input);

    assertThat(actual, is("\"000102030405\""));
  }

  @Test
  public void byteArrayHexDeserializerCanDeserializeHex() throws Exception {
    ObjectMapper mapper = deserMapper(new JacksonUtils.ByteArrayHexDeserializer());

    String input = "\"000102030405\"";
    byte[] actual = mapper.readValue(input, byte[].class);

    assertThat(actual, is(new byte[] {0, 1, 2, 3, 4, 5}));
  }

  @Test(expected = InvalidFormatException.class)
  public void byteArrayHexDeserializerThrowsIfGivenInvalidHex() throws Exception {
    ObjectMapper mapper = deserMapper(new JacksonUtils.ByteArrayHexDeserializer());

    mapper.readValue("\"0001g2030405\"", byte[].class);
  }

  private static <T> ObjectMapper serMapper(StdScalarSerializer<T> ser) {
    SimpleModule module = new SimpleModule().addSerializer(ser.handledType(), ser);
    return new ObjectMapper().registerModule(module);
  }

  @SuppressWarnings("unchecked")
  private static <T> ObjectMapper deserMapper(StdScalarDeserializer<T> deser) {
    SimpleModule module = new SimpleModule().addDeserializer((Class<T>) deser.handledType(), deser);
    return new ObjectMapper().registerModule(module);
  }
}
