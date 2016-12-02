package com.clxcommunications.xms;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.HttpContext;

import com.clxcommunications.xms.api.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An asynchronous consumer that consumes JSON objects.
 * 
 * @param <T>
 *            the expected type after deserialization
 */
class JsonApiAsyncConsumer<T> extends AsyncByteConsumer<T> {

	private final ObjectMapper json;
	private final Class<T> jsonClass;
	private HttpResponse response;
	private ByteInOutStream bios;

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
	protected void onByteReceived(ByteBuffer buf, IOControl ioctrl)
	        throws IOException {
		bios.write(buf);
	}

	@Override
	protected void onResponseReceived(HttpResponse response)
	        throws HttpException, IOException {
		this.response = response;

		/*
		 * We'll assume that most responses fit within 1KiB. For larger
		 * responses the output stream will grow automatically.
		 */
		this.bios = new ByteInOutStream(1024);
	}

	@Override
	protected T buildResult(HttpContext context) throws Exception {
		int code = response.getStatusLine().getStatusCode();
		InputStream inputStream = bios.toInputStream();

		switch (code) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_CREATED:
			return json.readValue(inputStream, jsonClass);
		case HttpStatus.SC_BAD_REQUEST:
		case HttpStatus.SC_FORBIDDEN:
			ApiError error = json.readValue(inputStream, ApiError.class);
			throw new ErrorResponseException(error);
		default:
			ContentType type =
			        ContentType.getLenient(response.getEntity());
			InputStreamEntity entity =
			        new InputStreamEntity(inputStream, bios.size(), type);
			response.setEntity(entity);
			throw new UnexpectedResponseException(response);
		}
	}

}
