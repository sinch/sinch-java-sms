package com.clxcommunications.xms.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class DeliveryStatusTest {

	@Property
	public void stringRepresentationIsIdentity(String str) throws Exception {
		assertThat(DeliveryStatus.of(str).status(), is(str));
	}

}
