package com.clxcommunications.xms;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule;

/**
 * A Jackson object mapper suitable for use with the CLX REST API objects.
 */
public class ApiObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an object mapper suitable for the CLX REST API. This mapper will
	 * serialize pretty printed JSON.
	 */
	public ApiObjectMapper() {
		this(true);
	}

	/**
	 * Creates an object mapper suitable for the CLX REST API.
	 * 
	 * @param prettyPrint
	 *            whether serialized JSON should be pretty printed
	 */
	public ApiObjectMapper(boolean prettyPrint) {
		registerModule(new ThreeTenModule());
		setSerializationInclusion(Include.NON_NULL);
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
	}

}
