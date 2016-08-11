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
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.util.AbstractDataWrapper;
import eu.alpinweiss.filegen.util.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;

import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link RangeGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class RangeGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;
	public static final String EMPTY = "";

	public RangeGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		synchronized (this) {
			final String pattern = fieldDefinition.getPattern();
			if (pattern == null || EMPTY.equals(pattern)) {
				valueVault.storeValue(new StringRangeDataWrapper());
				return;
			}

			final String[] split = pattern.split(",");
			if (split.length == 1) {
				valueVault.storeValue(new StringRangeDataWrapper() {
					@Override
					public String getStringValue() {
						return pattern;
					}
				});
				return;
			}

			final int i = ThreadLocalRandom.current().nextInt(0, split.length);
			valueVault.storeValue(new StringRangeDataWrapper() {
				@Override
				public String getStringValue() {
					return split[i].trim();
				}
			});
		}
	}

	private class StringRangeDataWrapper extends AbstractDataWrapper {
		@Override
		public FieldType getFieldType() {
			return FieldType.STRING;
		}
	}
}
