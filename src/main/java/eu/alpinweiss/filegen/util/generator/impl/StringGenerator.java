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
 * {@link StringGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class StringGenerator implements FieldGenerator {

	private final Generex generex;
	private final FieldDefinition fieldDefinition;

	public StringGenerator(FieldDefinition fieldDefinition) {
		this.generex = new Generex(fieldDefinition.getPattern());
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		synchronized (this) {
			if (Generate.Y.equals(fieldDefinition.getGenerate())) {
				valueVault.storeValue(new StringDataWrapper() {
					@Override
					public String getStringValue() {
						return generex.random();
					}
				});
			} else {
				valueVault.storeValue(new StringDataWrapper() {
					@Override
					public String getStringValue() {
						return fieldDefinition.getPattern();
					}
				});
			}
		}
	}

	private class StringDataWrapper extends AbstractDataWrapper {
		@Override
		public FieldType getFieldType() {
			return FieldType.STRING;
		}
	}
}
