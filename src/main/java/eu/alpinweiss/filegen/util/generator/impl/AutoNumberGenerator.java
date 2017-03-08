/*
 * Copyright 2016 Alexander Severgin
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
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link AutoNumberGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class AutoNumberGenerator implements FieldGenerator {

    private final FieldDefinition fieldDefinition;
    private int startNum;

    public AutoNumberGenerator(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
        final String pattern = this.fieldDefinition.getPattern();
        if (!pattern.isEmpty()) {
            startNum = Integer.parseInt(pattern);
        }
    }

    @Override
    public void generate(final ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
        valueVault.storeValue(new IntegerDataWrapper() {
            @Override
            public Double getNumberValue() {
                int value = startNum + (parameterVault.rowCount() * parameterVault.dataPartNumber())  + parameterVault.iterationNumber();
                return new Double(value);
            }
        });
    }

    private class IntegerDataWrapper extends AbstractDataWrapper {
        @Override
        public FieldType getFieldType() {
            return FieldType.INTEGER;
        }
    }
}
