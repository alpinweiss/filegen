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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.Iterator;
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
	private Map<Integer, Input2TableInfo> myTableInfoMap;

	public SheetProcessor(long rowCount, CountDownLatch startSignal, CountDownLatch doneSignal, CellStyle cs, SXSSFSheet sheet,
	                      int columnCount, Map<Integer, Input2TableInfo> myTableInfoMap) {
		this.rowCount = rowCount;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.cs = cs;
		this.sheet = sheet;
		this.columnCount = columnCount;
		this.myTableInfoMap = myTableInfoMap;
	}

	public SheetProcessor() {
	}

	@Override
	public void run() {
		try {
			startSignal.await();

			generateSheetData(rowCount, cs, sheet, columnCount, myTableInfoMap);

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

		Iterator<Integer> iterator = hashMap.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
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
				System.out.println(Thread.currentThread().getName() + " Processed " + i + " rows");
			}
			for (int colCount = 0; colCount < columnCount; colCount++) {

				final Cell dataCell = row.createCell(colCount);
				input2TableInfo = hashMap.get(colCount);

				input2TableInfo.generator().generate(i, randomGenerator, new ValueVault() {
					@Override
					public void storeValue(String value) {
						dataCell.setCellValue(value);
					}
				});
			}

		}
	}
}
