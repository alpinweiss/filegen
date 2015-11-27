/*
 * Copyright 2011 Alexander Severgin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.alpinweiss.filegen.util.impl;

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.util.FieldGenerator;
import eu.alpinweiss.filegen.util.ValueVault;

import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link FloatGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class FloatGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;

	public FloatGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(int iterationNo, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		String pattern = fieldDefinition.getPattern();
		if (pattern != null) {
			valueVault.storeValue(String.format(pattern, randomGenerator.nextDouble()));
		}
		valueVault.storeValue(Double.toString(randomGenerator.nextDouble()));
	}
}
