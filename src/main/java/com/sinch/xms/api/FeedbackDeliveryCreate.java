package com.sinch.xms.api;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = FeedbackDeliveryCreate.Builder.class)
@JsonTypeName("delivery_feedback")
public abstract class FeedbackDeliveryCreate {

    /**
     * A builder of delivery feedback.
     */
    public static class Builder extends FeedbackDeliveryCreateImpl.Builder {

        Builder() {
        }
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

    public abstract List<String> recipients();

}
