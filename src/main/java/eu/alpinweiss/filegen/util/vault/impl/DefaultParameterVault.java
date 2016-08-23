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
package eu.alpinweiss.filegen.util.vault.impl;

import eu.alpinweiss.filegen.util.vault.ParameterVault;

/**
 * {@link DefaultParameterVault}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class DefaultParameterVault implements ParameterVault {

    private int iterationNumber;
    private int dataPartNumber;
    private int rowCount;
    private int overrun;

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

    @Override
    public int overrun() {
        return overrun;
    }

    @Override
    public ParameterVault setOverrun(int overrun) {
        this.overrun = overrun;
        return this;
    }
}
