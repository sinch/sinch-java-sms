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

import javax.annotation.Nullable;
import org.apache.http.concurrent.FutureCallback;

/**
 * This interface provides a method for wrapping a {@link FutureCallback} such that additional logic
 * can be introduced to the callback.
 *
 * <p>This class also contains the predefined wrappers {@link #exceptionDropper} and {@link
 * #identity}.
 */
public interface CallbackWrapper {

  /**
   * A callback wrapper that catches and logs exceptions thrown within the wrapped callback at the
   * ERROR level.
   */
  public static final CallbackWrapper exceptionDropper = new DropExceptionWrapper();

  /** The identity callback wrapper. That is, it simply returns the input callback untouched. */
  public static final CallbackWrapper identity =
      new CallbackWrapper() {

        @Override
        public <T> FutureCallback<T> wrap(FutureCallback<T> callback) {
          return callback;
        }
      };

  /**
   * Wraps the given callback. It is acceptable to return <code>null</code> from this method, in
   * which case no callback will be called.
   *
   * @param callback the callback object to wrap
   * @param <T> the result type of the callback
   * @return a wrapped callback
   */
  @Nullable
  <T> FutureCallback<T> wrap(@Nullable FutureCallback<T> callback);
}
