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
import eu.alpinweiss.filegen.model.Generate;
import eu.alpinweiss.filegen.util.wrapper.AbstractDataWrapper;
import eu.alpinweiss.filegen.util.generator.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link DateGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class DateGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;

	public DateGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
		String pattern = fieldDefinition.getPattern();

		if (pattern == null || "".equals(pattern)) {
			if (Generate.Y.equals(fieldDefinition.getGenerate())) {
				valueVault.storeValue(new DateDataWrapper());
			} else {
				valueVault.storeValue(new DateDataWrapper() {
					@Override
					public Date getDateValue() {
						return null;
					}
				});
			}
		} else {
			if (Generate.Y.equals(fieldDefinition.getGenerate())) {
				final String[] split = pattern.split(":");
				if (split.length == 1) {
					valueVault.storeValue(new DateDataWrapper());
				}
				String dateFormatPattern = split[0];
				final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);

				valueVault.storeValue(new DateDataWrapper() {
					@Override
					public Date getDateValue() {
						try {
							return dateFormat.parse(split[1]);
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				});
			} else {
				valueVault.storeValue(new DateDataWrapper());
			}
		}
	}

	private class DateDataWrapper extends AbstractDataWrapper {

		@Override
		public FieldType getFieldType() {
			return FieldType.DATE;
		}

		@Override
		public Date getDateValue() {
			return new Date();
		}
	}

}
