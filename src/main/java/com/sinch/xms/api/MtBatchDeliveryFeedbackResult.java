package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchDeliveryFeedbackResult.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class MtBatchDeliveryFeedbackResult {

  /** Builder of batch delivery feedback results. */
  public static class Builder extends MtBatchDeliveryFeedbackResultImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link MtBatchDeliveryFeedbackResult} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final MtBatchDeliveryFeedbackResult.Builder builder() {
    return new MtBatchDeliveryFeedbackResult.Builder();
  }
}
