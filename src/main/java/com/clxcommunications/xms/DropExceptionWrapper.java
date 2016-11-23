package com.clxcommunications.xms;

import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A callback wrapper that catches and logs exceptions thrown within the wrapped
 * callback at the ERROR level. Once caught and logged the exceptions are
 * dropped.
 */
final class DropExceptionWrapper implements CallbackWrapper {

	private static final class WrappedCallback<T>
	        implements FutureCallback<T> {

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

	private static final Logger log =
	        LoggerFactory.getLogger(DropExceptionWrapper.class);

	/**
	 * The message format to use for the log message.
	 */
	private static final String msgfmt =
	        "caught and dropped exception in callback: {}";

	@Override
	public <T> FutureCallback<T> wrap(final FutureCallback<T> callback) {
		return (callback == null) ? null : new WrappedCallback<T>(callback);
	}

}
