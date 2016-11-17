package com.clxcommunications.xms;

import javax.annotation.Nullable;

import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interface provides a method for wrapping a {@link FutureCallback} such
 * that additional logic can be introduced to the callback.
 */
public interface CallbackWrapper {

	/**
	 * A callback wrapper that catches and logs exceptions thrown within the
	 * wrapped callback at the ERROR level.
	 */
	public static final class ExceptionDropper implements CallbackWrapper {

		private static final Logger log =
		        LoggerFactory.getLogger(ExceptionDropper.class);

		private static final String MSG =
		        "caught and dropped exception in callback: {}";

		@Override
		public <T> FutureCallback<T> wrap(
		        final FutureCallback<T> callback) {
			if (callback == null) {
				return null;
			}

			return new FutureCallback<T>() {

				@Override
				public void completed(T result) {
					try {
						callback.completed(result);
					} catch (Exception e) {
						log.error(MSG, e.getMessage(), e);
					}
				}

				@Override
				public void failed(Exception ex) {
					try {
						callback.failed(ex);
					} catch (Exception e) {
						log.error(MSG, e.getMessage(), e);
					}
				}

				@Override
				public void cancelled() {
					try {
						callback.cancelled();
					} catch (Exception e) {
						log.error(MSG, e.getMessage(), e);
					}
				}

			};
		}

	}

	/**
	 * A callback wrapper that catches and logs exceptions thrown within the
	 * wrapped callback at the ERROR level.
	 * 
	 * @see ExceptionDropper
	 */
	public static final CallbackWrapper exceptionDropper =
	        new ExceptionDropper();

	/**
	 * A callback wrapper that doesn't wrap, that is, it simply returns the
	 * input callback.
	 */
	public static final CallbackWrapper identity =
	        new CallbackWrapper() {

		        @Override
		        public <T> FutureCallback<T> wrap(FutureCallback<T> callback) {
			        return callback;
		        }

	        };

	/**
	 * Wraps the given callback. It is acceptable to return <code>null</code>
	 * from this method, in which case no callback will be called.
	 * 
	 * @param callback
	 *            the callback object to wrap
	 * @return a wrapped callback
	 */
	@Nullable
	<T> FutureCallback<T> wrap(@Nullable FutureCallback<T> callback);

}
