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

@Value.Immutable
@ValueStylePublic
public abstract class BatchDeliveryReportParams {

	/**
	 * The delivery report types that can be retrieved.
	 */
	public static enum ReportType {
		SUMMARY, FULL
	}

	public static final class Builder
	        extends BatchDeliveryReportParamsImpl.Builder {

		public Builder summaryReport() {
			return this.reportType(ReportType.SUMMARY);
		}

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

	@Nullable
	public abstract ReportType reportType();

	public abstract Set<DeliveryStatus> statuses();

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
