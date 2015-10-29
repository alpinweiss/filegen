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
import org.apache.poi.ss.usermodel.Cell;

import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link IntegerGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class IntegerGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;

	public IntegerGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(int iterationNo, ThreadLocalRandom randomGenerator, Cell cell) {
		String pattern = fieldDefinition.getPattern();
		if (pattern != null) {
			cell.setCellValue(String.format(pattern, randomGenerator.nextInt(Integer.MAX_VALUE)));
		}
		cell.setCellValue(new Integer(randomGenerator.nextInt()).toString());
	}
}