package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = MtBatchDeliveryFeedbackResult.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
abstract public class MtBatchDeliveryFeedbackResult {

    /**
     * Builder of batch delivery feedback results.
     */
    public static class Builder extends MtBatchDeliveryFeedbackResultImpl.Builder {

        Builder() {
        }
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
