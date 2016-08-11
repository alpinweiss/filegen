package eu.alpinweiss.filegen.util.impl;

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.util.AbstractDataWrapper;
import eu.alpinweiss.filegen.util.FieldGenerator;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class AutoNumberGenerator implements FieldGenerator {

    private final FieldDefinition fieldDefinition;
    private int startNum;

    public AutoNumberGenerator(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
        final String pattern = this.fieldDefinition.getPattern();
        if (!StringUtils.isEmpty(pattern)) {
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
