package com.clxcommunications.xms;

public class RuntimeApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuntimeApiException(ApiException e) {
		super(e);
	}

}
