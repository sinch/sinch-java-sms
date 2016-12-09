/*-
 * #%L
 * SDK for CLX XMS
 * %%
 * Copyright (C) 2016 CLX Communications
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
public class ThreeTenOffsetDateTimeGenerator
        extends Generator<OffsetDateTime> {

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
		java.time.OffsetDateTime time =
		        java8generator.generate(random, status);

		return OffsetDateTime.parse(time.toString());
	}

}
