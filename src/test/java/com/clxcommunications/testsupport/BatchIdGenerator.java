package com.clxcommunications.testsupport;

import com.clxcommunications.xms.api.BatchId;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

/**
 * Generator of batch IDs.
 */
public class BatchIdGenerator extends Generator<BatchId> {

	public BatchIdGenerator() {
		super(BatchId.class);
	}

	@Override
	public BatchId generate(SourceOfRandomness random,
	        GenerationStatus status) {
		return BatchId.of(gen().type(String.class).generate(random, status));
	}

}
