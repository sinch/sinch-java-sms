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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sinch.xms.BadRequestResponseException;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/**
 * API object containing an error response.
 *
 * @see BadRequestResponseException
 */
@Value.Immutable
@ValueStylePackageDirect
@JsonDeserialize(as = BadRequestErrorImpl.class)
public abstract class BadRequestError {

  /**
   * The timestamp of the error.
   *
   * @return a non-null string
   */
  public abstract String timestamp();

  /**
   * The machine readable HTTP status code.
   *
   * @return a non-null integer
   */
  public abstract Integer status();

  /**
   * The human readable HTTP error text for the status.
   *
   * @return a non-null string
   */
  public abstract String error();

  /**
   * The path of the request.
   *
   * @return a non-null string
   */
  public abstract String path();

  /**
   * Creates a new bad request error object from the given timestamp, status, error and path.
   *
   * @param timestamp the timestamp of the error
   * @param status the status code
   * @param error the error message
   * @param path the path
   * @return a non-null Bad request error object
   */
  @Nonnull
  public static BadRequestError of(String timestamp, Integer status, String error, String path) {
    return BadRequestErrorImpl.of(timestamp, status, error, path);
  }
}
