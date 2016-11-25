package com.clxcommunications.xms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.clxcommunications.xms.BatchDeliveryReportParams.ReportType;
import com.clxcommunications.xms.api.DeliveryStatus;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class BatchDeliveryReportParamsTest {

	@Test
	public void generatesExpectedQueryParametersMinimal() throws Exception {
		BatchDeliveryReportParams filter =
		        new BatchDeliveryReportParams.Builder().build();

		String actual = filter.toUrlEncodedQuery();
		String expected = "";

		assertThat(actual, is(expected));
	}

	@Test
	public void generatesExpectedQueryParametersMaximalish() throws Exception {
		BatchDeliveryReportParams filter =
		        new BatchDeliveryReportParams.Builder()
		                .fullReport()
		                .addStatus(DeliveryStatus.EXPIRED,
		                        DeliveryStatus.DELIVERED)
		                .addCode(100, 200, 300)
		                .build();

		String actual = filter.toUrlEncodedQuery();
		String expected = "type=full"
		        + "&status=Expired%2CDelivered"
		        + "&code=100%2C200%2C300";

		assertThat(actual, is(expected));
	}

	@Property
	public void generatesValidQueryParameters(ReportType reportType,
	        Set<Integer> codes) throws Exception {
		BatchDeliveryReportParams filter =
		        new BatchDeliveryReportParams.Builder()
		                .reportType(reportType)
		                .addStatus(DeliveryStatus.EXPIRED,
		                        DeliveryStatus.DELIVERED)
		                .codes(codes)
		                .build();

		String query = filter.toUrlEncodedQuery();

		// Will throw IllegalArgumentException if an invalid URI is attempted.
		URI.create("http://localhost/?" + query);
	}

}
