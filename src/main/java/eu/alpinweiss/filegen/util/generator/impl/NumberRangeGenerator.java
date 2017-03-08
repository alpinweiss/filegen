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
package eu.alpinweiss.filegen.util.generator.impl;

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.util.wrapper.AbstractDataWrapper;
import eu.alpinweiss.filegen.util.generator.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link NumberRangeGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class NumberRangeGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;

	public NumberRangeGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		synchronized (this) {
			final String pattern = fieldDefinition.getPattern();
			if (pattern.isEmpty()) {
				valueVault.storeValue(new NumberRangeDataWrapper());
				return;
			}

			String[] split = pattern.split(":");
			if (split.length == 1) {
				valueVault.storeValue(new NumberRangeDataWrapper() {
					@Override
					public Double getNumberValue() {
						return Double.valueOf(pattern);
					}
				});
				return;
			}
			int min = Integer.parseInt(split[0]);
			int max = Integer.parseInt(split[1]);
			final List<Integer> integers = new ArrayList<>();
			for (int i = min; i < max; i++) {
				integers.add(i);
			}
			final int index = ThreadLocalRandom.current().nextInt(0, integers.size());
			valueVault.storeValue(new NumberRangeDataWrapper() {
				@Override
				public Double getNumberValue() {
					return Double.valueOf(integers.get(index).toString());
				}
			});
		}
	}

	private class NumberRangeDataWrapper extends AbstractDataWrapper {
		@Override
		public FieldType getFieldType() {
			return FieldType.INTEGER;
		}
	}
}
