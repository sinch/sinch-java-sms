package com.clxcommunications.xms;

import org.junit.Test;

public class ApiConnectionTest {

	@Test
	public void canBuildWithAllCustoms() throws Exception {
		ApiConnection conn = ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpointHost("localhost", -1, null)
		        .endpointBasePath("/custompath")
		        .start();

		conn.close();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsBasePathWithoutInitialSlash() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpointHost("localhost", -1, null)
		        .endpointBasePath("custompath")
		        .start();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsBasePathWithQuestionMark() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpointHost("localhost", -1, null)
		        .endpointBasePath("/custom?path")
		        .start();
	}

}
