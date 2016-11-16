package com.clxcommunications.xms.api;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@ValueStylePackage
@JsonDeserialize(as = ImmutableTags.class)
public interface Tags {

	List<String> tags();

}
