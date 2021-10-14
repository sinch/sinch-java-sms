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

import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A callback wrapper that catches and logs exceptions thrown within the wrapped callback at the
 * ERROR level. Once caught and logged the exceptions are dropped.
 */
final class DropExceptionWrapper implements CallbackWrapper {

  private static final class WrappedCallback<T> implements FutureCallback<T> {

    private final FutureCallback<T> callback;

    private WrappedCallback(FutureCallback<T> callback) {
      this.callback = callback;
    }

    @Override
    public void completed(T result) {
      try {
        callback.completed(result);
      } catch (Exception e) {
        log.error(msgfmt, e.getMessage(), e);
      }
    }

    @Override
    public void failed(Exception ex) {
      try {
        callback.failed(ex);
      } catch (Exception e) {
        log.error(msgfmt, e.getMessage(), e);
      }
    }

    @Override
    public void cancelled() {
      try {
        callback.cancelled();
      } catch (Exception e) {
        log.error(msgfmt, e.getMessage(), e);
      }
    }
  }

  private static final Logger log = LoggerFactory.getLogger(DropExceptionWrapper.class);

  /** The message format to use for the log message. */
  private static final String msgfmt = "caught and dropped exception in callback: {}";

  @Override
  public <T> FutureCallback<T> wrap(final FutureCallback<T> callback) {
    return (callback == null) ? null : new WrappedCallback<T>(callback);
  }
}
