package com.clxcommunications.xms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(depluralize = true, from = "using", jdkOnly = true,
        overshadowImplementation = true, allParameters = true,
        visibility = ImplementationVisibility.PACKAGE, typeImmutable = "*Impl",
        defaults = @Value.Immutable(builder = false, copy = false),
        depluralizeDictionary = { "batch:batches", "status:statuses" })
@interface ValueStylePackageDirect {

}
