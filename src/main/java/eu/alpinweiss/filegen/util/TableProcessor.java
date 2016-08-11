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

import com.healthmarketscience.jackcess.Table;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.service.OutputWriterHolder;
import eu.alpinweiss.filegen.util.vault.ParameterVault;
import eu.alpinweiss.filegen.util.vault.ValueVault;
import eu.alpinweiss.filegen.util.vault.impl.DefaultParameterVault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link eu.alpinweiss.filegen.util.TableProcessor}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class TableProcessor implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger(TableProcessor.class);

	private ParameterVault parameterVault;
	private CountDownLatch startSignal;
	private CountDownLatch doneSignal;
	private Table table;
	private int columnCount;
	private Map<Integer, Input2TableInfo> input2TableInfoMap;
	private OutputWriterHolder outputWriterHolder;
	private Map<Table, List<Object[]>> tableToGeneratedData;

	public TableProcessor(ParameterVault parameterVault, CountDownLatch startSignal, CountDownLatch doneSignal, Table table,
	                      int columnCount, Map<Integer, Input2TableInfo> input2TableInfoMap, OutputWriterHolder outputWriterHolder,
	                      Map<Table, List<Object[]>> tableToGeneratedData) {
		this.parameterVault = parameterVault;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.table = table;
		this.columnCount = columnCount;
		this.input2TableInfoMap = input2TableInfoMap;
		this.outputWriterHolder = outputWriterHolder;
		this.tableToGeneratedData = tableToGeneratedData;
	}

	public TableProcessor() {
	}

	@Override
	public void run() {
		try {
			startSignal.await();

			generateTableData(parameterVault, table, columnCount, input2TableInfoMap, tableToGeneratedData);

			doneSignal.countDown();
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void generateTableData(ParameterVault parameterVault, Table table, int columnCount, Map<Integer, Input2TableInfo> hashMap, Map<Table, List<Object[]>> tableToGeneratedData) throws SQLException, IOException {
		// Generate column headings
		Input2TableInfo input2TableInfo;
		AccessValueVault accessValueVault = new AccessValueVault();
		List<Object[]> rowList = new ArrayList<>();

		ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
		for (int i = 1; i < parameterVault.rowCount()+1; i++) {
			if (i != 0 &&  i % 10000 == 0) {
				outputWriterHolder.writeValueInLine(Thread.currentThread().getName() + " Processed " + i + " rows");
			}
			final Object[] row = new Object[columnCount];
			accessValueVault.setRow(row);

			for (int colCount = 0; colCount < columnCount; colCount++) {
				input2TableInfo = hashMap.get(colCount);
				accessValueVault.setIndex(colCount);
				input2TableInfo.generator().generate(parameterVault.setIterationNumber(i), randomGenerator, accessValueVault);
			}
			rowList.add(row);
		}
		tableToGeneratedData.put(table, rowList);
	}

	class AccessValueVault implements ValueVault {

		private int index;
		private Object[] row;

		@Override
		public void storeValue(DataWrapper wrapper) {
			FieldType fieldType = wrapper.getFieldType();
			switch (fieldType) {
				case DATE:
					row[index] = wrapper.getDateValue();
					break;
				case FLOAT:
					row[index] = wrapper.getNumberValue();
					break;
				case INTEGER:
					row[index] = Integer.valueOf((int)Math.round(wrapper.getNumberValue()));
					break;
				default:
					row[index] = wrapper.getStringValue();
			}
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setRow(Object[] row) {
			this.row = row;
		}
	}
}
