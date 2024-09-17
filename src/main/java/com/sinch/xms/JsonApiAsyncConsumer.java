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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.sinch.xms.api.ApiError;
import com.sinch.xms.api.BadRequestError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

/**
 * An asynchronous consumer that consumes JSON objects.
 *
 * @param <T> the expected type after deserialization
 */
class JsonApiAsyncConsumer<T> extends AsyncByteConsumer<T> {

  private final ObjectMapper json;
  private final Class<T> jsonClass;
  private HttpResponse response;
  private ByteInOutStream bios;

  /**
   * Builds a new JSON consumer.
   *
   * @param json the object mapper
   * @param jsonClass the class that will be deserialized
   */
  public JsonApiAsyncConsumer(ObjectMapper json, Class<T> jsonClass) {
    this.json = json;
    this.jsonClass = jsonClass;
  }

  @Override
  protected void onByteReceived(ByteBuffer buf, IOControl ioctrl) throws IOException {
    bios.write(buf);
  }

  @Override
  protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {
    this.response = response;

    /*
     * We'll assume that most responses fit within 1KiB. For larger
     * responses the output stream will grow automatically.
     */
    this.bios = new ByteInOutStream(1024);
  }

  @Override
  protected T buildResult(HttpContext context) throws Exception {
    int code = response.getStatusLine().getStatusCode();
    InputStream inputStream = bios.toInputStream();

    switch (code) {
      case HttpStatus.SC_OK:
      case HttpStatus.SC_CREATED:
        return json.readValue(inputStream, jsonClass);
      case HttpStatus.SC_ACCEPTED:
        return null;
      case HttpStatus.SC_BAD_REQUEST:
      case HttpStatus.SC_FORBIDDEN:
        try {
          ApiError error = json.readValue(inputStream, ApiError.class);
          throw new ErrorResponseException(error);
        } catch (ValueInstantiationException e) {
          BadRequestError error = json.readValue(bios.toInputStream(), BadRequestError.class);
          throw new BadRequestResponseException(error);
        }
      case HttpStatus.SC_NOT_FOUND:
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        RequestLine rl = coreContext.getRequest().getRequestLine();
        throw new NotFoundException(rl.getUri());
      case HttpStatus.SC_UNAUTHORIZED:
        throw new UnauthorizedException();
      default:
        ContentType type = ContentType.getLenient(response.getEntity());
        InputStreamEntity entity = new InputStreamEntity(inputStream, bios.size(), type);
        response.setEntity(entity);
        throw new UnexpectedResponseException(response);
    }
  }
}
