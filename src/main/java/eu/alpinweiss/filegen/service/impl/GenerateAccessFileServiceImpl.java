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
package eu.alpinweiss.filegen.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.healthmarketscience.jackcess.*;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.service.GenerateAccessFileService;
import eu.alpinweiss.filegen.service.OutputWriterHolder;
import eu.alpinweiss.filegen.util.Input2TableInfo;
import eu.alpinweiss.filegen.util.TableProcessor;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.impl.DefaultParameterVault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * {@link GenerateAccessFileServiceImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
@Singleton
public class GenerateAccessFileServiceImpl implements GenerateAccessFileService {

	private final static Logger LOGGER = LogManager.getLogger(GenerateAccessFileServiceImpl.class);

	@Inject
	private OutputWriterHolder outputWriterHolder;

	@Override
	public void generateAccess(String accessFilename, int rowCount, List<FieldDefinition> fieldDefinitionList, int tableCount) {

		long startTime = new Date().getTime();

		outputWriterHolder.writeValueInLine("Access data generation started");
		Database db = null;

		try {
			db = DatabaseBuilder.create(Database.FileFormat.V2010, new File(accessFilename));
			//New table
			int columnCount = fieldDefinitionList.size();

			//Create Hash Map of Field Definitions
			Map<Integer, Input2TableInfo> input2TableInfoMap = new LinkedHashMap<>(columnCount);
			Map<Table, List<Object[]>> tableToGeneratedData = new HashMap<>();

			for (int i = 0; i < columnCount; i++) {
				Input2TableInfo input2TableInfo = new Input2TableInfo();
				FieldDefinition fieldDefinition = fieldDefinitionList.get(i);
				input2TableInfo.setFieldText(fieldDefinition.getFieldName());
				input2TableInfo.setFieldDefinition(fieldDefinition);
				input2TableInfo.initGenerator();
				input2TableInfoMap.put(i, input2TableInfo);
			}

			if (tableCount > 1) {
				List<Table> tableListForGeneration = new ArrayList<>();
				for (int i = 0; i < tableCount; i++) {
					TableBuilder tableBuilder = new TableBuilder("dataTable_" + i);
					for (Integer key : input2TableInfoMap.keySet()) {
						Input2TableInfo input2TableInfo = input2TableInfoMap.get(key);
						tableBuilder.addColumn(new ColumnBuilder(input2TableInfo.getFieldText()).setSQLType(getType(input2TableInfo.getFieldDefinition().getType())));
					}
					tableListForGeneration.add(tableBuilder.toTable(db));
				}

				CountDownLatch startSignal = new CountDownLatch(1);
				CountDownLatch doneSignal;

				doneSignal = new CountDownLatch(tableCount);

				ParameterVault parameterVault = new DefaultParameterVault(0, rowCount);
				TableProcessor tableProcessor1 = new TableProcessor(parameterVault, startSignal, doneSignal, tableListForGeneration.get(0), columnCount, input2TableInfoMap, outputWriterHolder, tableToGeneratedData);
				new Thread(tableProcessor1, "Processor-" + tableCount).start();

				for (int i = 1; i < tableCount; i++) {
					ParameterVault parameterVaultRest = new DefaultParameterVault(i, rowCount);
					TableProcessor tableProcessor = new TableProcessor(parameterVaultRest, startSignal, doneSignal,tableListForGeneration.get(i), columnCount, input2TableInfoMap, outputWriterHolder, tableToGeneratedData);
					new Thread(tableProcessor, "Processor-" + i).start();
				}

				startSignal.countDown();
				doneSignal.await();
			} else {
				TableBuilder tableBuilder = new TableBuilder("dataTable_0");
				for (Integer key : input2TableInfoMap.keySet()) {
					Input2TableInfo input2TableInfo = input2TableInfoMap.get(key);
					tableBuilder.addColumn(new ColumnBuilder(input2TableInfo.getFieldText()).setSQLType(getType(input2TableInfo.getFieldDefinition().getType())));
				}
				ParameterVault parameterVault = new DefaultParameterVault(0, rowCount);
				new TableProcessor().generateTableData(parameterVault, tableBuilder.toTable(db), columnCount, input2TableInfoMap, tableToGeneratedData);
			}


			outputWriterHolder.writeValueInLine("Access data generation finished.");
			long generationTime = new Date().getTime();
			outputWriterHolder.writeValueInLine("Time used " + ((generationTime - startTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Writing to file.");

			for (Map.Entry<Table, List<Object[]>> tableListEntry : tableToGeneratedData.entrySet()) {
				Table table = tableListEntry.getKey();
				List<Object[]> rowListForTable = tableListEntry.getValue();
				for (Object[] row : rowListForTable) {
					table.addRow(row);
				}
			}

			long writeTime = new Date().getTime();
			outputWriterHolder.writeValueInLine("Time used " + ((writeTime - generationTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Total time used " + ((writeTime - startTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Done");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (db != null) {
					db.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private int getType(FieldType type) {
		switch (type){
			case DATE:
				return Types.DATE;
			case FLOAT:
				return Types.FLOAT;
			case INTEGER:
				return Types.INTEGER;
			default:
				return Types.VARCHAR;
		}
	}
}
