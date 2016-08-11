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

import com.mifmif.common.regex.Generex;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.model.Generate;
import eu.alpinweiss.filegen.util.wrapper.AbstractDataWrapper;
import eu.alpinweiss.filegen.util.generator.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;

import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link FloatGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class FloatGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;
	private Generex generex;

	public FloatGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(ParameterVault parameterVault, final ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		synchronized (this) {
			final String pattern = fieldDefinition.getPattern();
			if (Generate.Y.equals(fieldDefinition.getGenerate())) {
				if (pattern != null && !"".equals(pattern)) {
					if (generex == null) {
						this.generex = new Generex(fieldDefinition.getPattern());
					}
					valueVault.storeValue(new FloatDataWrapper() {
						@Override
						public Double getNumberValue() {
							return Double.valueOf(generex.random());
						}
					});
				} else {
					valueVault.storeValue(new FloatDataWrapper() {
						@Override
						public Double getNumberValue() {
							return randomGenerator.nextDouble();
						}
					});
				}
			} else {
				if (pattern != null && !"".equals(pattern)) {
					valueVault.storeValue(new FloatDataWrapper() {
						@Override
						public Double getNumberValue() {
							return Double.valueOf(pattern);
						}
					});
				} else {
					valueVault.storeValue(new FloatDataWrapper());
				}
			}
		}
	}

	private class FloatDataWrapper extends AbstractDataWrapper {
		@Override
		public FieldType getFieldType() {
			return FieldType.FLOAT;
		}
	}
}
