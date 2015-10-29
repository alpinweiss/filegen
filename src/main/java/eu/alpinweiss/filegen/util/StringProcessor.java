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
import java.util.Date;
import java.util.List;
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

    private long iterationCount;

    private final List<String> stringList = new ArrayList<String>();
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    private final FileWriter fw;

    public StringProcessor(long iterationCount, CountDownLatch startSignal, CountDownLatch doneSignal, FileWriter fw) {
        this.iterationCount = iterationCount;
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
        this.fw = fw;
    }

    @Override
    public void run() {
        try {
            startSignal.await();

            ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            System.out.println(Thread.currentThread().getName() + " starts generating " + iterationCount + " rows");

            for (long i = 0; i < iterationCount; i++) {
                if (i != 0 && i % 10000 == 0) {
                    synchronized (fw) {
                        System.out.println(Thread.currentThread().getName() + " writes next " + i + " rows");
                        for (String row : stringList) {
                            fw.write(row);
                        }
                    }
                    stringList.clear();
                }
                String str = String.format("%s %.5f %05d %.5f %03d %s\r\n", Long.toHexString(Double.doubleToLongBits(randomGenerator.nextDouble())), randomGenerator.nextDouble(), randomGenerator.nextInt(9999), randomGenerator.nextDouble(), randomGenerator.nextInt(999), simpleDateFormat.format(new Date()));
                stringList.add(str);
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
        this.iterationCount += iterationCount;
    }
}