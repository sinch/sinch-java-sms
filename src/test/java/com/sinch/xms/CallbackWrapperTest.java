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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.junit.Assert.assertThat;

import org.apache.http.concurrent.FutureCallback;
import org.junit.Test;

public class CallbackWrapperTest {

  private final FutureCallback<Integer> exceptionalCallback =
      new FutureCallback<Integer>() {

        @Override
        public void completed(Integer result) {
          throw COMPLETED_EXCEPTION;
        }

        @Override
        public void failed(Exception ex) {
          throw FAILED_EXCEPTION;
        }

        @Override
        public void cancelled() {
          throw CANCELLED_EXCEPTION;
        }
      };

  private static final RuntimeException COMPLETED_EXCEPTION = new RuntimeException("completed");

  private static final RuntimeException FAILED_EXCEPTION = new RuntimeException("failed");

  private static final RuntimeException CANCELLED_EXCEPTION = new RuntimeException("cancelled");

  @Test
  public void identityCanWrapNull() throws Exception {
    assertThat(CallbackWrapper.identity.wrap(null), is(nullValue()));
  }

  @Test
  public void identityCanWrapNonNull() throws Exception {
    assertThat(
        CallbackWrapper.identity.wrap(exceptionalCallback), is(theInstance(exceptionalCallback)));
  }

  @Test
  public void dropperCanWrapNull() throws Exception {
    assertThat(CallbackWrapper.exceptionDropper.wrap(null), is(nullValue()));
  }
}
