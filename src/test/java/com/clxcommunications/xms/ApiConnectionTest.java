package com.clxcommunications.xms;

import org.junit.Test;

public class ApiConnectionTest {

	@Test
	public void canBuildWithAllCustoms() throws Exception {
		ApiConnection conn = ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath")
		        .start();

		conn.close();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsEndpointWithQueryComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath?query=yes")
		        .start();
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsBasePathWithFragmentComponent() throws Exception {
		ApiConnection.builder()
		        .token("token")
		        .username("username")
		        .endpoint("https://localhost:3000/basepath#fragment")
		        .start();
	}

}
