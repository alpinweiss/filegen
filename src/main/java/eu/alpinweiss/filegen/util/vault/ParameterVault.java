package eu.alpinweiss.filegen.util.vault;

public interface ParameterVault {
    int iterationNumber();
    int dataPartNumber();
    int rowCount();

    ParameterVault setIterationNumber(int iterationNumber);
}
