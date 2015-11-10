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
package eu.alpinweiss.filegen.service.impl;

import com.google.inject.Singleton;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.service.GenerateAdvancedFileService;
import eu.alpinweiss.filegen.util.Input2TableInfo;
import eu.alpinweiss.filegen.util.StringProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link GenerateAdvancedFileServiceImpl}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
@Singleton
public class GenerateAdvancedFileServiceImpl implements GenerateAdvancedFileService {

    private final static Logger LOGGER = LogManager.getLogger(GenerateAdvancedFileServiceImpl.class);
    public static final int THREAD_COUNT = 10;

    @Override
    public void generateFile(String filename, long rowCount, List<FieldDefinition> fieldDefinitionList) {

        long startTime = new Date().getTime();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal;

        try {
            File newTextFile = new File(filename);
            FileWriter fw = new FileWriter(newTextFile);

            doneSignal = new CountDownLatch(THREAD_COUNT);
            long iterationCount = rowCount / THREAD_COUNT;
            long iterationMod = rowCount % THREAD_COUNT;

	        int columnCount = fieldDefinitionList.size();

	        //Create Hash Map of Field Definitions
	        LinkedHashMap<Integer, Input2TableInfo> tableInfoHashMap = new LinkedHashMap<>(columnCount);

	        for (int i = 0; i < columnCount; i++) {
		        Input2TableInfo input2TableInfo = new Input2TableInfo();
		        FieldDefinition fieldDefinition = fieldDefinitionList.get(i);
		        input2TableInfo.setFieldText(fieldDefinition.getFieldName());
		        input2TableInfo.setFieldDefinition(fieldDefinition);
		        input2TableInfo.initGenerator();
		        tableInfoHashMap.put(i, input2TableInfo);
	        }


            for (int i = 0; i < THREAD_COUNT; i++) {
                StringProcessor stringProcessor = new StringProcessor(iterationCount, startSignal, doneSignal, fw, columnCount, tableInfoHashMap);
                if (i == 0) {
                    stringProcessor.addIterationCount(iterationMod);
                }
                new Thread(stringProcessor, "Processor-" + i).start();
            }

            startSignal.countDown();
            doneSignal.await();

            fw.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        System.out.println("Done");
        System.out.println("Time used " + ((new Date().getTime() - startTime) / 1000) + " sec");
    }

}
