package com.clxcommunications.xms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;

/**
 * A default style for public immutable value types.
 * 
 * Note, this is a duplicate of
 * {@link com.clxcommunications.xms.api.ValueStylePublic} to be able to keep it
 * at package visibility.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(depluralize = true, from = "using", jdkOnly = true,
        overshadowImplementation = true, typeImmutable = "*Impl",
        depluralizeDictionary = { "batch:batches", "status:statuses" })
@interface ValueStylePublic {

}
