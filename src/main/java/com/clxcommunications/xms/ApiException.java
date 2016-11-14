package com.clxcommunications.xms;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String code;
	private final String text;

	ApiException(ApiError error) {
		super(error.text());

		this.code = error.code();
		this.text = error.text();
	}

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

}
