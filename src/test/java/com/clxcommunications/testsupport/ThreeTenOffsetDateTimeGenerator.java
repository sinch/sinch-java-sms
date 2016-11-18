package com.clxcommunications.testsupport;

import org.threeten.bp.OffsetDateTime;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.java.time.OffsetDateTimeGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

/**
 * Generator of ThreeTen OffsetDateTime. Acts as a thin wrapper of the Java 8
 * OffsetDateTime generator.
 */
public class ThreeTenOffsetDateTimeGenerator extends Generator<OffsetDateTime> {

	private final OffsetDateTimeGenerator java8generator =
	        new OffsetDateTimeGenerator();

	public ThreeTenOffsetDateTimeGenerator() {
		super(OffsetDateTime.class);
	}

	public void configure(InRange range) {
		java8generator.configure(range);
	}

	@Override
	public OffsetDateTime generate(SourceOfRandomness random,
	        GenerationStatus status) {
		java.time.OffsetDateTime time = java8generator.generate(random, status);

		return OffsetDateTime.parse(time.toString());
	}

}
