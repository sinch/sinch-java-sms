/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.clxcommunications.xms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.immutables.value.Value;

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

		Builder() {
		}

		/**
		 * Request a summary delivery report.
		 * 
		 * @return this builder for use in a chained invocation
		 * @see ReportType#SUMMARY
		 */
		public Builder summaryReport() {
			return this.reportType(ReportType.SUMMARY);
		}

		/**
		 * Request a full delivery report.
		 * 
		 * @return this builder for use in a chained invocation
		 * @see ReportType#FULL
		 */
		public Builder fullReport() {
			return this.reportType(ReportType.FULL);
		}

	}

	/**
	 * Creates a builder of {@link BatchDeliveryReportParams} instances.
	 * 
	 * @return a builder
	 */
	@Nonnull
	public static final BatchDeliveryReportParams.Builder builder() {
		return new Builder();
	}

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
	 * @return a list of query parameters
	 */
	@Nonnull
	List<NameValuePair> toQueryParams() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(6);

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

		return params;
	}

}
