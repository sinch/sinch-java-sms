package com.clxcommunications.xms;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.threeten.bp.LocalDate;

@Value.Immutable
@ValueStylePublic
public abstract class BatchFilter {

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

	@Nullable
	public abstract LocalDate startDate();

	@Nullable
	public abstract LocalDate endDate();

	public abstract Set<String> originators();

	public abstract Set<String> tags();

}
