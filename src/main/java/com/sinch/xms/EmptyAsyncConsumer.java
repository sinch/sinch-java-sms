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
import java.nio.CharBuffer;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An asynchronous consumer that consumes empty responses. If a non-empty response is received with
 * a success status code (2xx), then a warning is logged.
 */
class EmptyAsyncConsumer extends AsyncCharConsumer<Void> {

  private static final Logger log = LoggerFactory.getLogger(EmptyAsyncConsumer.class);

  private final ObjectMapper json;
  private HttpResponse response;
  private StringBuilder sb;

  /**
   * Builds a new empty body consumer.
   *
   * @param json the object mapper
   */
  public EmptyAsyncConsumer(ObjectMapper json) {
    this.json = json;
  }

  @Override
  protected void onCharReceived(CharBuffer buf, IOControl ioctrl) throws IOException {
    sb.append(buf.toString());
  }

  @Override
  protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {
    this.response = response;
    this.sb = new StringBuilder();
  }

  @Override
  protected Void buildResult(HttpContext context) throws Exception {
    int code = response.getStatusLine().getStatusCode();
    String content = sb.toString();

    switch (code) {
      case HttpStatus.SC_OK:
      case HttpStatus.SC_CREATED:
        if (!content.isEmpty()) {
          log.warn("Expected empty body but got '{}'", content);
        }
        return null;
      case HttpStatus.SC_BAD_REQUEST:
      case HttpStatus.SC_FORBIDDEN:
        try {
          ApiError error = json.readValue(content, ApiError.class);
          throw new ErrorResponseException(error);
        } catch (ValueInstantiationException e) {
          BadRequestError error = json.readValue(content, BadRequestError.class);
          throw new BadRequestResponseException(error);
        }
      case HttpStatus.SC_NOT_FOUND:
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        RequestLine rl = coreContext.getRequest().getRequestLine();
        throw new NotFoundException(rl.getUri());
      case HttpStatus.SC_UNAUTHORIZED:
        throw new UnauthorizedException();
      default:
        ContentType contentType = ContentType.getLenient(response.getEntity());
        response.setEntity(new StringEntity(content, contentType));
        throw new UnexpectedResponseException(response);
    }
  }
}
