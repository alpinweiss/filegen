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
package eu.alpinweiss.filegen.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link StringProcessor}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class StringProcessor implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger(StringProcessor.class);

    private long rowCount;

    private final List<String> stringList = new ArrayList<String>();
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    private final FileWriter fw;
	private int columnCount;
	private Map<Integer, Input2TableInfo> input2TableInfoMap;

	public StringProcessor(long rowCount, CountDownLatch startSignal, CountDownLatch doneSignal, FileWriter fw,
	                       int columnCount, Map<Integer, Input2TableInfo> input2TableInfoMap) {
        this.rowCount = rowCount;
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
        this.fw = fw;
		this.columnCount = columnCount;
		this.input2TableInfoMap = input2TableInfoMap;
	}

    @Override
    public void run() {
        try {
            startSignal.await();
            ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
            System.out.println(Thread.currentThread().getName() + " starts generating " + rowCount + " rows");

            for (long i = 0; i < rowCount; i++) {
                if (i != 0 && i % 10000 == 0) {
                    synchronized (fw) {
                        System.out.println(Thread.currentThread().getName() + " writes next " + i + " rows");
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

		            input2TableInfo.generator().generate((int)i, randomGenerator, new ValueVault() {
			            @Override
			            public void storeValue(String value) {
				            builder.append(value).append(" ");
			            }
		            });
	            }

	            builder.append("\r\n");
                stringList.add(builder.toString());
            }
            synchronized (fw) {
                System.out.println(Thread.currentThread().getName() + " writes last " + stringList.size() + " rows");
                for (String row : stringList) {
                    fw.write(row);
                }
            }
            doneSignal.countDown();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void addIterationCount(long iterationCount) {
        this.rowCount += iterationCount;
    }
}