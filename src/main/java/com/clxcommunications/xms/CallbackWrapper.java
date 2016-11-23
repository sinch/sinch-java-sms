package com.clxcommunications.xms;

import javax.annotation.Nullable;

import org.apache.http.concurrent.FutureCallback;

/**
 * This interface provides a method for wrapping a {@link FutureCallback} such
 * that additional logic can be introduced to the callback.
 * <p>
 * This class also contains the predefined wrappers {@link #exceptionDropper}
 * and {@link #identity}.
 */
public interface CallbackWrapper {

	/**
	 * A callback wrapper that catches and logs exceptions thrown within the
	 * wrapped callback at the ERROR level.
	 * 
	 * @see DropExceptionWrapper
	 */
	public static final CallbackWrapper exceptionDropper =
	        new DropExceptionWrapper();

	/**
	 * The identity callback wrapper. That is, it simply returns the input
	 * callback untouched.
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
	 * @param <T>
	 *            the result type of the callback
	 * @return a wrapped callback
	 */
	@Nullable
	<T> FutureCallback<T> wrap(@Nullable FutureCallback<T> callback);

}
