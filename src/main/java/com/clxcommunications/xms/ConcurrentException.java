package com.clxcommunications.xms;

import java.util.concurrent.ExecutionException;

/**
 * An exception that wraps checked exceptions. It is used in the SDK when
 * forcing a future and unwrapping the potential {@link ExecutionException}.
 */
public class ConcurrentException extends ApiException {

	private static final long serialVersionUID = 1L;

	public ConcurrentException(Throwable e) {
		super(e);
	}

}
