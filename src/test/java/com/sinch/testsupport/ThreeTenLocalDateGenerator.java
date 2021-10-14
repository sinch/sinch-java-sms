/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
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
package com.sinch.testsupport;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.java.time.LocalDateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.threeten.bp.LocalDate;

/** Generator of ThreeTen LocalDate. Acts as a thin wrapper of the Java 8 LocalDate generator. */
public class ThreeTenLocalDateGenerator extends Generator<LocalDate> {

  private final LocalDateGenerator java8generator = new LocalDateGenerator();

  public ThreeTenLocalDateGenerator() {
    super(LocalDate.class);
  }

  public void configure(InRange range) {
    java8generator.configure(range);
  }

  @Override
  public LocalDate generate(SourceOfRandomness random, GenerationStatus status) {
    java.time.LocalDate time = java8generator.generate(random, status);

    return LocalDate.parse(time.toString());
  }
}
