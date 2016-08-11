package eu.alpinweiss.filegen.util.vault.impl;

import eu.alpinweiss.filegen.util.vault.ParameterVault;

public class DefaultParameterVault implements ParameterVault {

    private int iterationNumber;
    private int dataPartNumber;
    private int rowCount;

    public DefaultParameterVault(int dataPartNumber, int rowCount) {
        this.dataPartNumber = dataPartNumber;
        this.rowCount = rowCount;
    }

    @Override
    public int iterationNumber() {
        return iterationNumber;
    }

    @Override
    public int dataPartNumber() {
        return dataPartNumber;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    @Override
    public ParameterVault setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
        return this;
    }
}
