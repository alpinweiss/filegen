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

import eu.alpinweiss.filegen.command.steps.ReadInputParametersStep;
import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

			System.out.println("Iterations: " + iterationCount + " lineSeparator" + lineSeparator);
            List<Object[]> fields = new ArrayList<Object[]>(sheet.getLastRowNum()-3);

			for (int i = 4; i <= sheet.getLastRowNum(); i++) {

				XSSFRow row = sheet.getRow(i);
                Object[] fieldDefinition = new Object[row.getLastCellNum()];

				for (int y = 0; y < row.getLastCellNum(); y++) {
					XSSFCell cell = row.getCell(y);

					if (cell == null) {
                        fieldDefinition[y] = null;
						break;
					}

					switch(cell.getCellType()) {
						case Cell.CELL_TYPE_BLANK:
                            System.out.print("\t\t");
                            fieldDefinition[y] = null;
                            break;
						case Cell.CELL_TYPE_BOOLEAN:
							System.out.print(cell.getBooleanCellValue() + "\t\t");
                            fieldDefinition[y] = cell.getBooleanCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							System.out.print(cell.getNumericCellValue() + "\t\t");
                            fieldDefinition[y] = cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
							System.out.print(cell.getStringCellValue() + "\t\t");
                            fieldDefinition[y] = cell.getStringCellValue();
							break;
					}
				}
                fields.add(fieldDefinition);
				System.out.println("");
			}

			file.close();

            for (Object[] field : fields) {
                FieldDefinition fieldDefinition = new FieldDefinition();
                String name = getStringName(field[0]);
                fieldDefinition.setFieldName(name);
                fieldDefinition.setType((String) field[1]);
                Double fieldLength = (Double) field[2];
                fieldDefinition.setLength(fieldLength != null ? fieldLength.intValue() : 0);
                fieldDefinition.setPattern((String) field[3]);
                model.getFieldDefinitionList().add(fieldDefinition);
            }

            model.setRowCount(iterationCount);
            model.setLineSeparator(lineSeparator);
            model.setOutputFileName(outputFileName);

            System.out.println("");

		} catch (FileNotFoundException e) {
			LOGGER.error("Can't read input parameters file", e);
		} catch (IOException e) {
			LOGGER.error("Error while reading xlsx file", e);
		}
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
        return null;
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
