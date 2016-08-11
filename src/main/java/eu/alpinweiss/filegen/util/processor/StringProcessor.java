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
package eu.alpinweiss.filegen.util.processor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import eu.alpinweiss.filegen.service.OutputWriterHolder;
import eu.alpinweiss.filegen.util.wrapper.DataWrapper;
import eu.alpinweiss.filegen.util.Input2TableInfo;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;
import eu.alpinweiss.filegen.util.vault.impl.DefaultParameterVault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link StringProcessor}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class StringProcessor implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger(StringProcessor.class);

    private ParameterVault parameterVault;

    private final List<String> stringList = new ArrayList<>();
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    private final FileWriter fw;
	private final int columnCount;
	private final Map<Integer, Input2TableInfo> input2TableInfoMap;
	private OutputWriterHolder outputWriterHolder;

	public StringProcessor(ParameterVault parameterVault, CountDownLatch startSignal, CountDownLatch doneSignal, FileWriter fw,
	                       int columnCount, Map<Integer, Input2TableInfo> input2TableInfoMap, OutputWriterHolder outputWriterHolder) {
        this.parameterVault = parameterVault;
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
        this.fw = fw;
		this.columnCount = columnCount;
		this.input2TableInfoMap = input2TableInfoMap;
		this.outputWriterHolder = outputWriterHolder;
	}

    @Override
    public void run() {
        try {
            startSignal.await();
            ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
	        outputWriterHolder.writeValueInLine(Thread.currentThread().getName() + " starts generating " + parameterVault.rowCount() + " rows");

            for (int i = 0; i < parameterVault.rowCount(); i++) {
                if (i != 0 && i % 10000 == 0) {
                    synchronized (fw) {
	                    outputWriterHolder.writeValueInLine(Thread.currentThread().getName() + " writes next " + i + " rows");
                        for (String row : stringList) {
                            fw.write(row);
                        }
                    }
                    stringList.clear();
                }

	            Input2TableInfo input2TableInfo;
	            final StringBuilder builder = new StringBuilder();
	            for (int colCount = 0; colCount < columnCount; colCount++) {
		            input2TableInfo = input2TableInfoMap.get(colCount);

		            input2TableInfo.generator().generate(parameterVault.setIterationNumber(i), randomGenerator, new ValueVault() {
			            @Override
			            public void storeValue(DataWrapper value) {
				            builder.append(value.getStringValue()).append(" ");
			            }
		            });
	            }

	            builder.append("\r\n");
                stringList.add(builder.toString());
            }
            synchronized (fw) {
	            outputWriterHolder.writeValueInLine(Thread.currentThread().getName() + " writes last " + stringList.size() + " rows");
                for (String row : stringList) {
                    fw.write(row);
                }
            }
            doneSignal.countDown();
        } catch (InterruptedException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void addIterationCount(int iterationCount) {
        this.parameterVault = new DefaultParameterVault(parameterVault.dataPartNumber(), parameterVault.rowCount() + iterationCount);
    }
}