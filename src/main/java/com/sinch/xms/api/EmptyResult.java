package com.sinch.xms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(builder = EmptyResult.Builder.class)
public class EmptyResult {
    public static final class Builder extends EmptyResultImpl.Builder {

        Builder() {
        }
    }
}
