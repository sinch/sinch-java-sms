package com.clxcommunications.xms.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;

/**
 * A default style for public immutable value types.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(depluralize = true, from = "using", jdkOnly = true,
        overshadowImplementation = true,
        depluralizeDictionary = { "batch:batches" }, typeImmutable = "*Impl")
@interface ValueStylePublic {

}
