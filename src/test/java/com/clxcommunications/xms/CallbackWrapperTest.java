package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.apache.http.concurrent.FutureCallback;
import org.junit.Rule;
import org.junit.Test;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

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

	private static final RuntimeException COMPLETED_EXCEPTION =
	        new RuntimeException("completed");

	private static final RuntimeException FAILED_EXCEPTION =
	        new RuntimeException("failed");

	private static final RuntimeException CANCELLED_EXCEPTION =
	        new RuntimeException("cancelled");

	@Rule
	public TestLoggerFactoryResetRule testLoggerFactoryResetRule =
	        new TestLoggerFactoryResetRule();

	TestLogger logger = TestLoggerFactory
	        .getTestLogger(DropExceptionWrapper.class);

	@Test
	public void identityCanWrapNull() throws Exception {
		assertThat(CallbackWrapper.identity.wrap(null),
		        is(nullValue()));
	}

	@Test
	public void identityCanWrapNonNull() throws Exception {
		assertThat(CallbackWrapper.identity.wrap(exceptionalCallback),
		        is(theInstance(exceptionalCallback)));
	}

	@Test
	public void dropperCanWrapNull() throws Exception {
		assertThat(CallbackWrapper.exceptionDropper.wrap(null),
		        is(nullValue()));
	}

	@Test
	public void dropperLogsCompleted() throws Exception {
		FutureCallback<Integer> wrapped =
		        CallbackWrapper.exceptionDropper.wrap(exceptionalCallback);

		wrapped.completed(13);

		assertThat(logger.getLoggingEvents(),
		        is(Arrays.asList(
		                LoggingEvent.error(COMPLETED_EXCEPTION,
		                        "caught and dropped exception in callback: {}",
		                        COMPLETED_EXCEPTION.getMessage()))));
	}

	@Test
	public void dropperLogsFailed() throws Exception {
		FutureCallback<Integer> wrapped =
		        CallbackWrapper.exceptionDropper.wrap(exceptionalCallback);

		wrapped.failed(null);

		assertThat(logger.getLoggingEvents(),
		        is(Arrays.asList(
		                LoggingEvent.error(FAILED_EXCEPTION,
		                        "caught and dropped exception in callback: {}",
		                        FAILED_EXCEPTION.getMessage()))));
	}

	@Test
	public void dropperLogsCancelled() throws Exception {
		FutureCallback<Integer> wrapped =
		        CallbackWrapper.exceptionDropper.wrap(exceptionalCallback);

		wrapped.cancelled();

		assertThat(logger.getLoggingEvents(),
		        is(Arrays.asList(
		                LoggingEvent.error(CANCELLED_EXCEPTION,
		                        "caught and dropped exception in callback: {}",
		                        CANCELLED_EXCEPTION.getMessage()))));
	}

}
