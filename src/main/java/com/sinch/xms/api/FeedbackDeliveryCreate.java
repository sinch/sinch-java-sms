package com.sinch.xms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = FeedbackDeliveryCreate.Builder.class)
public abstract class FeedbackDeliveryCreate {

  /** A builder of delivery feedback. */
  public static class Builder extends FeedbackDeliveryCreateImpl.Builder {

    Builder() {}
  }

  /**
   * Creates a builder of {@link FeedbackDeliveryCreate} instances.
   *
   * @return a builder
   */
  @Nonnull
  public static final FeedbackDeliveryCreate.Builder builder() {
    return new Builder();
  }

  /**
   * The list of recipients. Can be set as an empty list to indicate that all recipients received
   * the message. If the feedback was enabled for a group, at least one phone number is required.
   *
   * @return a list of strings containing recipients
   */
  public abstract List<String> recipients();
}
