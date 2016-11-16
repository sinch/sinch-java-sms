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

		/*
		 * From jackson-datatype-threetenbp README.md:
		 * 
		 * "Most JSR-310 types are serialized as numbers (integers or decimals
		 * as appropriate) if the SerializationFeature#WRITE_DATES_AS_TIMESTAMPS
		 * feature is enabled, and otherwise are serialized in standard ISO-8601
		 * string representation."
		 */
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
	}

}
