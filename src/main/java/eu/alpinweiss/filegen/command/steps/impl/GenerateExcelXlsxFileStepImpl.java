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
package eu.alpinweiss.filegen.command.steps.impl;

import eu.alpinweiss.filegen.command.steps.GenerateExcelXlsxFileStep;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.Model;
import eu.alpinweiss.filegen.util.MyTableInfo;
import eu.alpinweiss.filegen.util.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link GenerateExcelXlsxFileStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class GenerateExcelXlsxFileStepImpl implements GenerateExcelXlsxFileStep {

    private final static Logger LOGGER = LogManager.getLogger(GenerateExcelXlsxFileStepImpl.class);

    RandomStringGenerator randomStringGenerator = new RandomStringGenerator();

    @Override
    public void execute(Model model) {
        String outputFileName = model.getOutputFileName();
        generateExcel(outputFileName, model.getRowCount(), model.getFieldDefinitionList());
        model.getFieldDefinitionList().clear();
    }

    private void generateExcel(String excelFilename, long rowCount, List<FieldDefinition> fieldDefinitionList) {

        long startTime = new Date().getTime();

        System.out.println("Excel data generation started");

        try {
            //New Workbook
            Workbook wb = new SXSSFWorkbook();
            Cell cell;

            //Cell style for header row
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(IndexedColors.LIME.getIndex());
            cs.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            Font f = wb.createFont();
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);
            f.setFontHeightInPoints((short) 12);
            cs.setFont(f);

            //New Sheet
            SXSSFSheet sheet1 = (SXSSFSheet) wb.createSheet("myData");

            int columnCount = fieldDefinitionList.size();

            //Create Hash Map of Field Definitions
            LinkedHashMap<Integer, MyTableInfo> hashMap = new LinkedHashMap<Integer, MyTableInfo>(columnCount);

            for (int i = 0; i < columnCount; i++) {
                MyTableInfo db2TableInfo = new MyTableInfo();
                FieldDefinition fieldDefinition = fieldDefinitionList.get(i);
                db2TableInfo.setFieldText(fieldDefinition.getFieldName());
                db2TableInfo.setFieldDefinition(fieldDefinition);
                hashMap.put(i, db2TableInfo);
            }

            // Row and column indexes
            int idx = 0;
            int idy = 0;

            // Generate column headings
            Row row = sheet1.createRow(idx);
            MyTableInfo db2TableInfo;

            Iterator<Integer> iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                Integer key = iterator.next();
                db2TableInfo = hashMap.get(key);
                cell = row.createCell(idy);
                cell.setCellValue(db2TableInfo.getFieldText());
                cell.setCellStyle(cs);
                sheet1.setColumnWidth(idy, (db2TableInfo.getFieldText().trim().length() * 500));
                idy++;
            }

            ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
//            for (int i = 1; i < 1048575; i++) {  // max possible rows
//            for (int i = 1; i < 150000; i++) {
            for (int i = 1; i < rowCount; i++) {

                row = sheet1.createRow(i);
                System.out.println("Processed " + i + " rows");
                for (int colCount = 0; colCount < columnCount; colCount++) {

                    cell = row.createCell(colCount);
                    db2TableInfo = hashMap.get(colCount);

                    cell.setCellValue(generateFieldValue(db2TableInfo.getFieldDefinition(), i, randomGenerator));
                }

            }

            System.out.println("Excel data generation finished.");
            long generationTime = new Date().getTime();
            System.out.println("Time used " + ((generationTime - startTime) / 1000) + " sec");
            System.out.println("Writing to file.");

            FileOutputStream fileOut = new FileOutputStream(excelFilename.trim());

            wb.write(fileOut);
            fileOut.close();

            long writeTime = new Date().getTime();
            System.out.println("Time used " + ((writeTime - generationTime) / 1000) + " sec");
            System.out.println("Total time used " + ((writeTime - startTime) / 1000) + " sec");
            System.out.println("Done");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private String generateFieldValue(FieldDefinition fieldDefinition, int i, ThreadLocalRandom randomGenerator) {
        String type = fieldDefinition.getType();
        if ("String".equalsIgnoreCase(type)) {
            String pattern = fieldDefinition.getPattern();
            if (pattern != null) {
                return String.format(pattern, i);
            }
            if (fieldDefinition.getLength() > 0) {
                return randomStringGenerator.generateRandomString(fieldDefinition.getLength());
            }
            return randomStringGenerator.generateRandomString();
        } else if ("Float".equalsIgnoreCase(type)) {
            String pattern = fieldDefinition.getPattern();
            if (pattern != null) {
                return String.format(pattern, randomGenerator.nextDouble());
            }
            return new Double(randomGenerator.nextDouble()).toString();
        } else if ("Integer".equalsIgnoreCase(type)) {
            String pattern = fieldDefinition.getPattern();
            if (pattern != null) {
                return String.format(pattern, randomGenerator.nextInt());
            }
            return new Integer(randomGenerator.nextInt()).toString();
        } else if ("Date".equalsIgnoreCase(type)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fieldDefinition.getPattern());
            return simpleDateFormat.format(new Date());
        }
        return "";
    }
}
