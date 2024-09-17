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

import com.sinch.xms.api.BadRequestError;

/**
 * Exception representing a non api error response from XMS. This exception is thrown when some fundamental
 * contract of the XMS API has been broken.
 *
 * <p>For information about specific errors please refer to the <a href=
 * "https://developers.sinch.com/docs/sms/api-reference/status-codes/">XMS API documentation</a>.
 */
public class BadRequestResponseException extends ApiException {

  private static final long serialVersionUID = 1L;

  private final Integer status;
  private final String path;
  private final String timestamp;
  private final String error;

  BadRequestResponseException(BadRequestError error) {
    super(error.path());

    this.status = error.status();
    this.path = error.path();
    this.timestamp = error.timestamp();
    this.error = error.error();
  }

  /**
   * The machine readable HTTP status code.
   *
   * @return the status code.
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * The path of the request.
   *
   * @return the path text
   */
  public String getPath() {
    return path;
  }

  /**
   * The timestamp of the error.
   *
   * @return the timestamp as a string.
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * The human readable HTTP error text for the status.
   *
   * @return the error text
   */
  public String getError() {
    return error;
  }
}
