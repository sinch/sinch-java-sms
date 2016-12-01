package com.clxcommunications.xms;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.protocol.HttpContext;

import com.clxcommunications.xms.api.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An asynchronous consumer that consumes JSON objects.
 * 
 * @param <T>
 *            the expected type after deserialization
 */
class JsonApiAsyncConsumer<T> extends AsyncCharConsumer<T> {

	private final ObjectMapper json;
	private final Class<T> jsonClass;
	private HttpResponse response;
	private StringBuilder sb;

	/**
	 * Builds a new JSON consumer.
	 * 
	 * @param json
	 *            the object mapper
	 * @param jsonClass
	 *            the class that will be deserialized
	 */
	public JsonApiAsyncConsumer(ObjectMapper json, Class<T> jsonClass) {
		this.json = json;
		this.jsonClass = jsonClass;
	}

	@Override
	protected CharsetDecoder createDecoder(ContentType contentType) {
		/*
		 * Force an UTF-8 decoder for JSON since the XMS doesn't explicitly say
		 * the encoding.
		 */
		if ("application/json".equals(contentType.getMimeType())) {
			return Charset.forName("UTF-8").newDecoder();
		} else {
			return super.createDecoder(contentType);
		}
	}

	@Override
	protected void onCharReceived(CharBuffer buf, IOControl ioctrl)
	        throws IOException {
		sb.append(buf.toString());
	}

	@Override
	protected void onResponseReceived(HttpResponse response)
	        throws HttpException, IOException {
		this.response = response;
		this.sb = new StringBuilder();
	}

	@Override
	protected T buildResult(HttpContext context) throws Exception {
		int code = response.getStatusLine().getStatusCode();
		String content = sb.toString();

		switch (code) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_CREATED:
			return json.readValue(content, jsonClass);
		case HttpStatus.SC_BAD_REQUEST:
		case HttpStatus.SC_FORBIDDEN:
			ApiError error = json.readValue(content, ApiError.class);
			throw new ErrorResponseException(error);
		default:
			// TODO: Good idea to buffer the response in this case?
			ContentType contentType =
			        ContentType.getLenient(response.getEntity());
			response.setEntity(
			        new StringEntity(content, contentType));
			throw new UnexpectedResponseException(response);
		}
	}

}
