package com.clxcommunications.testsupport;

import org.threeten.bp.LocalDate;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.java.time.LocalDateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

/**
 * Generator of ThreeTen LocalDate. Acts as a thin wrapper of the Java 8
 * LocalDate generator.
 */
public class ThreeTenLocalDateGenerator extends Generator<LocalDate> {

	private final LocalDateGenerator java8generator =
	        new LocalDateGenerator();

	public ThreeTenLocalDateGenerator() {
		super(LocalDate.class);
	}

	public void configure(InRange range) {
		java8generator.configure(range);
	}

	@Override
	public LocalDate generate(SourceOfRandomness random,
	        GenerationStatus status) {
		java.time.LocalDate time = java8generator.generate(random, status);

		return LocalDate.parse(time.toString());
	}

}
