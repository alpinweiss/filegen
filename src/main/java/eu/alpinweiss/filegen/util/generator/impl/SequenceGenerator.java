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
import eu.alpinweiss.filegen.util.generator.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;
import eu.alpinweiss.filegen.util.wrapper.AbstractDataWrapper;
import org.apache.commons.lang.StringUtils;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link SequenceGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class SequenceGenerator implements FieldGenerator {

    public static final String SEQUENCE_WITH_SUFFIX_AND_PREFIX = ".*\\[SEQ\\(\\s*\\d+\\,\\s*\\d+\\,\\s*(FAIL|RESTART)\\s*\\)\\].*";
    public static final String SEQUENCE = "\\[SEQ\\(\\s*\\d+\\,\\s*\\d+\\,\\s*(FAIL|RESTART)\\s*\\)\\]";

    private final FieldDefinition fieldDefinition;
    private int startNum;
    private int maxValue;
    private int digitCount;
    private String[] sequencePattern;
    private boolean shouldFail;

    public SequenceGenerator(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
        final String pattern = this.fieldDefinition.getPattern();
        if (!StringUtils.isEmpty(pattern) && pattern.matches(SEQUENCE_WITH_SUFFIX_AND_PREFIX)) {
            sequencePattern = cropPattern(pattern);

            final Scanner scanner = new Scanner(sequencePattern[1]);
            scanner.useDelimiter("\\D+");
            startNum = scanner.nextInt();
            digitCount = scanner.nextInt();
            maxValue = Integer.parseInt(StringUtils.repeat("9", digitCount));
            shouldFail = sequencePattern[1].contains("FAIL");
        }
    }

    String[] cropPattern(String pattern) {
        final String[] generationPattern = new String[3];

        final Pattern compile = Pattern.compile(SEQUENCE);
        final Matcher matcher = compile.matcher(pattern);

        if (matcher.find()) {
            generationPattern[0] = pattern.substring(0, matcher.start());
            generationPattern[1] = pattern.substring(matcher.start(), matcher.end());
            generationPattern[2] = pattern.substring(matcher.end(), pattern.length());
        } else {
            generationPattern[0] = "";
            generationPattern[1] = pattern;
            generationPattern[2] = "";
        }
        return generationPattern;
    }

    @Override
    public void generate(final ParameterVault parameterVault, ThreadLocalRandom randomGenerator, ValueVault valueVault) {
        valueVault.storeValue(new StringDataWrapper() {
            @Override
            public String getStringValue() {
                int value = startNum + (parameterVault.rowCount() * parameterVault.dataPartNumber()) + parameterVault.iterationNumber();
                if (value > maxValue && shouldFail) {
                    throw new RuntimeException("Sequence Generator Exception. Value: " + value + " meets limit: " + maxValue);
                }
                return sequencePattern[0] + StringUtils.leftPad(Integer.toString(value), digitCount, '0') + sequencePattern[2];
            }
        });
    }

    private class StringDataWrapper extends AbstractDataWrapper {
        @Override
        public FieldType getFieldType() {
            return FieldType.STRING;
        }
    }
}
