package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/** A description of the messages having a given delivery state. */
@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = Status.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public abstract class Status {

  /** A builder of batch delivery report statuses. */
  public static class Builder extends StatusImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link Status} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final Status.Builder builder() {
    return new Builder();
  }

  /**
   * The delivery status code.
   *
   * @return a status code
   */
  public abstract int code();

  /**
   * The delivery status for this bucket.
   *
   * @return a non-null delivery status
   */
  public abstract DeliveryStatus status();

  /**
   * The number of individual messages in this status bucket.
   *
   * @return a positive integer
   */
  public abstract int count();

  /**
   * The recipients having this status. Note, this is non-empty only if a <em>full</em> delivery
   * report has been requested.
   *
   * @return a non-null list of recipients
   */
  public abstract List<String> recipients();
}
