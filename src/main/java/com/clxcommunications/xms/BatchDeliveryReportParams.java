package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

import com.clxcommunications.xms.api.BatchId;
import com.clxcommunications.xms.api.DeliveryStatus;

/**
 * Describes the a filter that restricts and controls how a batch delivery
 * report is returned.
 */
@Value.Immutable
@ValueStylePackage
public abstract class BatchDeliveryReportParams {

	/**
	 * The delivery report types that can be retrieved.
	 */
	public static enum ReportType {
		/**
		 * Indicates a summary batch delivery report. The summary delivery
		 * report does not include the per-recipient result but rather
		 * aggregated statistics about the deliveries.
		 */
		SUMMARY,

		/**
		 * Indicates a full batch delivery report. This includes per-recipient
		 * delivery results. For batches with many destinations such reports may
		 * be very large.
		 */
		FULL
	}

	/**
	 * A builder of batch delivery report filters.
	 */
	public static class Builder extends BatchDeliveryReportParamsImpl.Builder {

		/**
		 * Request a summary delivery report.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder summaryReport() {
			return this.reportType(ReportType.SUMMARY);
		}

		/**
		 * Request a full delivery report.
		 * 
		 * @return this builder for use in a chained invocation
		 */
		public Builder fullReport() {
			return this.reportType(ReportType.FULL);
		}

	}

	/**
	 * The requested number of entries per page. A non-positive value means that
	 * the default value will be used.
	 * 
	 * @return the desired page size
	 */
	@Value.Default
	public int pageSize() {
		return 0;
	}

	/**
	 * Batch ID whose delivery report should be retrieved.
	 * 
	 * @return a non-null batch ID
	 */
	public abstract BatchId batchId();

	/**
	 * The desired report type. If not set then the default report type is used.
	 * 
	 * @return the desired report type or <code>null</code> if default
	 */
	@Nullable
	public abstract ReportType reportType();

	/**
	 * A set of delivery statuses that should be fetches.
	 * 
	 * @return the delivery statuses
	 */
	public abstract Set<DeliveryStatus> statuses();

	/**
	 * A set of status codes that should be fetches.
	 * 
	 * @return the status codes
	 */
	public abstract Set<Integer> codes();

	/**
	 * Formats this filter as an URL encoded list of query parameters.
	 * 
	 * @param page
	 *            the page to request
	 * @return a non-null string containing query parameters
	 */
	@Nonnull
	String toUrlEncodedQuery(int page) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(6);

		params.add(new BasicNameValuePair("page", String.valueOf(page)));

		if (pageSize() > 0) {
			params.add(new BasicNameValuePair("page_size",
			        String.valueOf(pageSize())));
		}

		params.add(new BasicNameValuePair("id", batchId().id()));

		if (reportType() != null) {
			params.add(new BasicNameValuePair("type",
			        reportType().name().toLowerCase(Locale.US)));
		}

		if (!statuses().isEmpty()) {
			ArrayList<String> statusStrings =
			        new ArrayList<String>(statuses().size());

			for (DeliveryStatus s : statuses()) {
				statusStrings.add(s.status());
			}

			params.add(new BasicNameValuePair("status",
			        Utils.join(",", statusStrings)));
		}

		if (!codes().isEmpty()) {
			ArrayList<String> codeStrings =
			        new ArrayList<String>(codes().size());

			for (Integer code : codes()) {
				codeStrings.add(code.toString());
			}

			params.add(new BasicNameValuePair("code",
			        Utils.join(",", codeStrings)));
		}

		return URLEncodedUtils.format(params, Consts.UTF_8);
	}

}
