/*
 *  Copyright 2015 Alexander Severgin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.alpinweiss.filegen.command.steps.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.model.Generate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.alpinweiss.filegen.command.steps.ReadInputParametersStep;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.Model;

/**
 * {@link ReadInputParametersStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class ReadInputParametersStepImpl implements ReadInputParametersStep {

	private final static Logger LOGGER = LogManager.getLogger(ReadInputParametersStepImpl.class);

	@Override
	public void execute(Model model) {
		String parameter = model.getParameter(INPUT_PARAMETER);
        model.getFieldDefinitionList().clear();

		try {
			FileInputStream file = new FileInputStream(new File(parameter));

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);

			long iterationCount = readIterationCount(sheet);
			String lineSeparator = readLineSeparator(sheet);
            String outputFileName = readOutputFileName(sheet);
			int sheetCount = readSheetCount(sheet);

			System.out.println("Iterations: " + iterationCount + " lineSeparator" + lineSeparator);
            List<Object[]> fields = new ArrayList<Object[]>(sheet.getLastRowNum()-4);

			for (int i = 5; i <= sheet.getLastRowNum(); i++) {

				XSSFRow row = sheet.getRow(i);
                Object[] fieldDefinition = new Object[row.getLastCellNum()];

				for (int y = 0; y < row.getLastCellNum(); y++) {
					XSSFCell cell = row.getCell(y);

					if (cell == null) {
                        fieldDefinition[y] = null;
						break;
					}

					cell.setCellType(Cell.CELL_TYPE_STRING);
					fieldDefinition[y] = cell.toString();
				}
                fields.add(fieldDefinition);
			}

			file.close();

            for (Object[] field : fields) {
                FieldDefinition fieldDefinition = new FieldDefinition();
                String name = getStringName(field[0]);
                fieldDefinition.setFieldName(name);
	            String fieldType = (String) field[1];
	            fieldDefinition.setType(fieldType != null ? FieldType.valueOf(fieldType.toUpperCase()) : FieldType.STRING);
                String fieldNeedToGenerate = (String) field[2];
                fieldDefinition.setGenerate(fieldNeedToGenerate != null ? Generate.valueOf(fieldNeedToGenerate.toUpperCase()) : Generate.N);
                if ((field.length > 3)) {
	                if (field[3] != null && field[3] instanceof Number) {
		                fieldDefinition.setPattern(field[3].toString());
	                } else {
		                fieldDefinition.setPattern((String) field[3]);
	                }
                }
                model.getFieldDefinitionList().add(fieldDefinition);
            }

            model.setRowCount(iterationCount);
            model.setLineSeparator(lineSeparator);
            model.setOutputFileName(outputFileName);
			model.setSheetCount(sheetCount);

            System.out.println("");
            
            workbook.close();

		} catch (FileNotFoundException e) {
			LOGGER.error("Can't read input parameters file", e);
		} catch (IOException e) {
			LOGGER.error("Error while reading xlsx file", e);
		}
	}

	private int readSheetCount(XSSFSheet sheet) {
		XSSFRow row = sheet.getRow(3);
		Cell cell = row.getCell(1);
		return (int) cell.getNumericCellValue();
	}

	private String readOutputFileName(XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(2);
        Cell cell = row.getCell(1);
        return cell.getStringCellValue();
    }

    private String getStringName(Object o) {
        if (o instanceof Number) {
            return o.toString();
        }
        return (String) o;
    }

    private long readIterationCount(XSSFSheet sheet) {
		XSSFRow row = sheet.getRow(0);
		Cell cell = row.getCell(1);
		return (long) cell.getNumericCellValue();
	}
	private String readLineSeparator(XSSFSheet sheet) {
		XSSFRow row = sheet.getRow(1);
		Cell cell = row.getCell(1);
		return cell.getStringCellValue();
	}
}
