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
import eu.alpinweiss.filegen.model.Model;
import eu.alpinweiss.filegen.util.MyTableInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * {@link GenerateExcelXlsxFileStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class GenerateExcelXlsxFileStepImpl implements GenerateExcelXlsxFileStep {

    private final static Logger LOGGER = LogManager.getLogger(GenerateExcelXlsxFileStepImpl.class);

    public static final int COLUMN_COUNT = 300;

    @Override
    public void execute(Model model) {
        generateExcel("testfile.xlsx");
    }

    private void generateExcel(String excelFilename) {

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

            //Create Hash Map of Field Definitions
            LinkedHashMap<Integer, MyTableInfo> hashMap = new LinkedHashMap<Integer, MyTableInfo>(COLUMN_COUNT);

            for (int i = 0; i < COLUMN_COUNT; i++) {
                MyTableInfo db2TableInfo = new MyTableInfo();
                db2TableInfo.setFieldName("Column" + i);
                db2TableInfo.setFieldText("test" + i);
                db2TableInfo.setFieldSize(5);
                db2TableInfo.setFieldDecimal(0);
                db2TableInfo.setFieldType(1);
//                db2TableInfo.setCellStyle(getCellAttributes(wb, cell, db2TableInfo));
                hashMap.put(i, db2TableInfo);
            }

            // Row and column indexes
            int idx = 0;
            int idy = 0;

            // Generate column headings
            Row row = sheet1.createRow(idx);
            MyTableInfo db2TableInfo = new MyTableInfo();

            Iterator<Integer> iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                Integer key = iterator.next();
                db2TableInfo = hashMap.get(key);
                cell = row.createCell(idy);
                cell.setCellValue(db2TableInfo.getFieldText());
                cell.setCellStyle(cs);
                if (db2TableInfo.getFieldSize() > db2TableInfo.getFieldText().trim().length()) {
                    sheet1.setColumnWidth(idy, (db2TableInfo.getFieldSize() * 500));
                } else {
                    sheet1.setColumnWidth(idy, (db2TableInfo.getFieldText().trim().length() * 500));
                }
                idy++;
            }

//            for (int i = 1; i < 1048575; i++) {  // max possible rows
            for (int i = 1; i < 200000; i++) {

                row = sheet1.createRow(i);
                System.out.println("Processed " + i + " rows");
                for (int colCount = 0; colCount < COLUMN_COUNT; colCount++) {

                    cell = row.createCell(colCount);
                    db2TableInfo = hashMap.get(colCount);

                    switch (db2TableInfo.getFieldType()) {
                        case 1:
                            cell.setCellValue(UUID.randomUUID().toString());
                            break;
                        case 2:
                            cell.setCellValue(UUID.randomUUID().toString());
                            break;
                        case 3:
                            cell.setCellValue(UUID.randomUUID().toString());
                            break;
                        default:
                            cell.setCellValue(UUID.randomUUID().toString());
                            break;
                    }
                    cell.setCellStyle(db2TableInfo.getCellStyle());
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

    private static CellStyle getCellAttributes(Workbook wb, Cell c, MyTableInfo db2TableInfo) {

        CellStyle cs = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        Font f = wb.createFont();

        switch (db2TableInfo.getFieldDecimal()) {
            case 1:
                cs.setDataFormat(df.getFormat("#,##0.0"));
                break;
            case 2:
                cs.setDataFormat(df.getFormat("#,##0.00"));
                break;
            case 3:
                cs.setDataFormat(df.getFormat("#,##0.000"));
                break;
            case 4:
                cs.setDataFormat(df.getFormat("#,##0.0000"));
                break;
            case 5:
                cs.setDataFormat(df.getFormat("#,##0.00000"));
                break;
            default:
                break;
        }

        cs.setFont(f);

        return cs;

    }
}
