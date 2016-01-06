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

import com.google.inject.Inject;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.service.GenerateXlsxFileService;
import eu.alpinweiss.filegen.service.OutputWriterHolder;
import eu.alpinweiss.filegen.util.Input2TableInfo;
import eu.alpinweiss.filegen.util.SheetProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * {@link GenerateXlsxFileServiceImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 *
 */
public class GenerateXlsxFileServiceImpl implements GenerateXlsxFileService {

	private final static Logger LOGGER = LogManager.getLogger(GenerateXlsxFileServiceImpl.class);

	@Inject
	private OutputWriterHolder outputWriterHolder;

	public void generateExcel(String excelFilename, long rowCount, List<FieldDefinition> fieldDefinitionList, int sheetCount) {

		long startTime = new Date().getTime();

		outputWriterHolder.writeValueInLine("Excel data generation started");

		//New Workbook
		Workbook wb = new SXSSFWorkbook();

		try {
			//Cell style for header row
			CellStyle cs = wb.createCellStyle();
			cs.setFillForegroundColor(IndexedColors.LIME.getIndex());
			cs.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			Font f = wb.createFont();
			f.setBoldweight(Font.BOLDWEIGHT_BOLD);
			f.setFontHeightInPoints((short) 12);
			cs.setFont(f);

			//New Sheet
			SXSSFSheet sheet1 = (SXSSFSheet) wb.createSheet("dataSheet");

			int columnCount = fieldDefinitionList.size();

			//Create Hash Map of Field Definitions
			Map<Integer, Input2TableInfo> input2TableInfoMap = new LinkedHashMap<>(columnCount);

			for (int i = 0; i < columnCount; i++) {
				Input2TableInfo input2TableInfo = new Input2TableInfo();
				FieldDefinition fieldDefinition = fieldDefinitionList.get(i);
				input2TableInfo.setFieldText(fieldDefinition.getFieldName());
				input2TableInfo.setFieldDefinition(fieldDefinition);
				input2TableInfo.initCellStyle(wb);
				input2TableInfo.initGenerator();
				input2TableInfoMap.put(i, input2TableInfo);
			}

			if (sheetCount > 1) {
				CountDownLatch startSignal = new CountDownLatch(1);
				CountDownLatch doneSignal;

				doneSignal = new CountDownLatch(sheetCount);

				SheetProcessor stringProcessorSheet1 = new SheetProcessor(rowCount, startSignal, doneSignal, cs, sheet1, columnCount, input2TableInfoMap, outputWriterHolder);
				new Thread(stringProcessorSheet1, "Processor-" + sheetCount).start();

				for (int i = 0; i < sheetCount-1; i++) {
					SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("dataSheet_" + i);
					SheetProcessor stringProcessor = new SheetProcessor(rowCount, startSignal, doneSignal, cs, sheet, columnCount, input2TableInfoMap, outputWriterHolder);
					new Thread(stringProcessor, "Processor-" + i).start();
				}

				startSignal.countDown();
				doneSignal.await();
			} else {
				new SheetProcessor().generateSheetData(rowCount, cs, sheet1, columnCount, input2TableInfoMap);
			}


			outputWriterHolder.writeValueInLine("Excel data generation finished.");
			long generationTime = new Date().getTime();
			outputWriterHolder.writeValueInLine("Time used " + ((generationTime - startTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Writing to file.");

			FileOutputStream fileOut = new FileOutputStream(excelFilename.trim());

			wb.write(fileOut);
			fileOut.close();

			long writeTime = new Date().getTime();
			outputWriterHolder.writeValueInLine("Time used " + ((writeTime - generationTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Total time used " + ((writeTime - startTime) / 1000) + " sec");
			outputWriterHolder.writeValueInLine("Done");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

	}
}
