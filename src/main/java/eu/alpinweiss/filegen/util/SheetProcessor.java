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

import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.service.OutputWriterHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link SheetProcessor}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class SheetProcessor implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger(SheetProcessor.class);

	private long rowCount;
	private CountDownLatch startSignal;
	private CountDownLatch doneSignal;
	private CellStyle cs;
	private SXSSFSheet sheet;
	private int columnCount;
	private Map<Integer, Input2TableInfo> input2TableInfoMap;
	private OutputWriterHolder outputWriterHolder;

	public SheetProcessor(long rowCount, CountDownLatch startSignal, CountDownLatch doneSignal, CellStyle cs, SXSSFSheet sheet,
	                      int columnCount, Map<Integer, Input2TableInfo> input2TableInfoMap, OutputWriterHolder outputWriterHolder) {
		this.rowCount = rowCount;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.cs = cs;
		this.sheet = sheet;
		this.columnCount = columnCount;
		this.input2TableInfoMap = input2TableInfoMap;
		this.outputWriterHolder = outputWriterHolder;
	}

	public SheetProcessor() {
	}

	@Override
	public void run() {
		try {
			startSignal.await();

			generateSheetData(rowCount, cs, sheet, columnCount, input2TableInfoMap);

			doneSignal.countDown();
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void generateSheetData(long rowCount, CellStyle cs, SXSSFSheet sheet, int columnCount, Map<Integer, Input2TableInfo> hashMap) {
		Cell cell;// Row and column indexes
		int idx = 0;
		int idy = 0;

		// Generate column headings
		Row row = sheet.createRow(idx);
		Input2TableInfo input2TableInfo;

		for (Integer key : hashMap.keySet()) {
			input2TableInfo = hashMap.get(key);
			cell = row.createCell(idy);
			cell.setCellValue(input2TableInfo.getFieldText());
			cell.setCellStyle(cs);
			sheet.setColumnWidth(idy, (input2TableInfo.getFieldText().trim().length() * 500));
			idy++;
		}

		ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
		for (int i = 1; i < rowCount; i++) {

			row = sheet.createRow(i);
			if (i != 0 &&  i % 10000 == 0) {
				outputWriterHolder.writeValueInLine(Thread.currentThread().getName() + " Processed " + i + " rows");
			}
			for (int colCount = 0; colCount < columnCount; colCount++) {

				final Cell dataCell = row.createCell(colCount);
				input2TableInfo = hashMap.get(colCount);

				final CellStyle cellStyle = input2TableInfo.getCellStyle();

				input2TableInfo.generator().generate(i, randomGenerator, new ValueVault() {
					@Override
					public void storeValue(DataWrapper wrapper) {
						FieldType fieldType = wrapper.getFieldType();
						switch (fieldType) {
							case DATE:
								dataCell.setCellStyle(cellStyle);
								dataCell.setCellValue(wrapper.getDateValue());
								break;
							case FLOAT:
							case INTEGER:
								dataCell.setCellType(Cell.CELL_TYPE_NUMERIC);
								dataCell.setCellValue(wrapper.getNumberValue());
								break;
							default:
								dataCell.setCellValue(wrapper.getStringValue());
						}

					}
				});
			}

		}
	}
}
