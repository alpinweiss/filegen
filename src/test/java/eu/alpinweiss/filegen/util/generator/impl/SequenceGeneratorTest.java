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
import eu.alpinweiss.filegen.util.vault.ValueVault;
import eu.alpinweiss.filegen.util.vault.impl.DefaultParameterVault;
import eu.alpinweiss.filegen.util.wrapper.DataWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link SequenceGeneratorTest}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class SequenceGeneratorTest {

    SequenceGenerator generator;

    @Before
    public void setUp() throws Exception {
        final FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setPattern("[SEQ(0,2,FAIL)]");
        generator = new SequenceGenerator(fieldDefinition);
    }

    @Test
    public void shouldMatch() {
        Pattern pattern = Pattern.compile(SequenceGenerator.SEQUENCE_WITH_SUFFIX_AND_PREFIX);
        assertTrue(pattern.matcher("A0012_[SEQ(9,5,FAIL)]").matches());
        assertTrue(pattern.matcher("A0012_[SEQ(9,5,RESTART)]").matches());
        assertTrue(pattern.matcher("A0012_[SEQ(9,100,FAIL)]").matches());
        assertTrue(pattern.matcher("A0012_[SEQ(0,5,FAIL)]_ASD").matches());
        assertTrue(pattern.matcher("A [SEQ(9,5,FAIL)] 013").matches());
        assertTrue(pattern.matcher("A[ [SEQ(9,5,FAIL)] 013").matches());
    }

    @Test
    public void testCropPattern() throws Exception {
        String[] strings = generator.cropPattern("[SEQ(9,5,FAIL)]");
        assertEquals("", strings[0]);
        assertEquals("[SEQ(9,5,FAIL)]", strings[1]);
        assertEquals("", strings[2]);

        strings = generator.cropPattern("A[ [SEQ(9,5,FAIL)] 013");
        assertEquals("A[ ", strings[0]);
        assertEquals("[SEQ(9,5,FAIL)]", strings[1]);
        assertEquals(" 013", strings[2]);
    }

    @Test
    public void testGeneration() {
        final DefaultParameterVault parameterVault = new DefaultParameterVault(0, 5);
        final String[] expectedValues = new String[] {"00", "01", "02", "03", "04"};
        for (int i = 0; i < 5; i++) {
            parameterVault.setIterationNumber(i);
            generator.generate(parameterVault, null, new ValueVault() {
                @Override
                public void storeValue(DataWrapper value) {
                    assertEquals(expectedValues[parameterVault.iterationNumber()], value.getStringValue());
                }
            });
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGenerationFail() {
        final DefaultParameterVault parameterVault = new DefaultParameterVault(80, 5);
        for (int i = 0; i < 30; i++) {
            parameterVault.setIterationNumber(i);
            generator.generate(parameterVault, null, new ValueVault() {
                @Override
                public void storeValue(DataWrapper value) {
                    value.getStringValue(); // need to call method for testing
                }
            });
        }
    }

    @Test
    public void testGenerationReset() {
        final String[] expectedValues = new String[] {"5", "6", "7", "8", "9", "1", "2", "3", "4", "5"};
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setPattern("[SEQ(5,1,RESTART)]");
        SequenceGenerator generator = new SequenceGenerator(fieldDefinition);
        final DefaultParameterVault parameterVault = new DefaultParameterVault(0, 5);
        for (int i = 0; i < 10; i++) {
            parameterVault.setIterationNumber(i);
            generator.generate(parameterVault, null, new ValueVault() {
                @Override
                public void storeValue(DataWrapper value) {
                    assertEquals(expectedValues[parameterVault.iterationNumber()], value.getStringValue());
                }
            });
        }
    }
}