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
        allParameters = true, depluralizeDictionary = { "batch:batches" },
        visibility = ImplementationVisibility.PACKAGE,
        defaults = @Value.Immutable(builder = false, copy = false))
public @interface ValueStylePackageDirect {

}
