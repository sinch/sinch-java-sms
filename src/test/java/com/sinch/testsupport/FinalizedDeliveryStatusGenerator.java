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
import com.pholser.junit.quickcheck.internal.generator.EnumGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.sinch.xms.api.DeliveryStatus;
import com.sinch.xms.api.FinalizedDeliveryStatus;

/** Generator of final delivery statuses. */
public class FinalizedDeliveryStatusGenerator extends Generator<FinalizedDeliveryStatus> {

  public FinalizedDeliveryStatusGenerator() {
    super(FinalizedDeliveryStatus.class);
  }

  @Override
  public FinalizedDeliveryStatus generate(
      SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
    return FinalizedDeliveryStatus.of(
        DeliveryStatus.of(
            new EnumGenerator(Status.class).generate(sourceOfRandomness, generationStatus).name()));
  }

  enum Status {
    Queued,
    Dispatched,
    Aborted,
    Rejected,
    Delivered,
    Failed,
    Expired,
    Unknown
  }
}
