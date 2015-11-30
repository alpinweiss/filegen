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

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.FieldType;
import eu.alpinweiss.filegen.model.Generate;
import eu.alpinweiss.filegen.util.impl.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * {@link Input2TableInfo}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class Input2TableInfo {

	String fieldText = null;
	FieldDefinition fieldDefinition;
	FieldGenerator fieldGenerator;
	CellStyle cellStyle;

	public String getFieldText() {
		return fieldText;
	}

	public void setFieldText(String fieldText) {
		this.fieldText = fieldText;
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

	public void setFieldDefinition(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	public FieldGenerator generator() {
		return fieldGenerator;
	}

	public void initGenerator() {
		FieldType type = fieldDefinition.getType();
		switch (type) {
			case FLOAT:
				this.fieldGenerator = new FloatGenerator(fieldDefinition);
				break;
			case STRING:
				this.fieldGenerator = new StringGenerator(fieldDefinition);
				break;
			case INTEGER:
				this.fieldGenerator = new IntegerGenerator(fieldDefinition);
				break;
			case DATE:
				this.fieldGenerator = new DateGenerator(fieldDefinition);
				break;
			case RANGE:
				this.fieldGenerator = new RangeGenerator(fieldDefinition);
				break;
			case NUMBERRANGE:
				this.fieldGenerator = new NumberRangeGenerator(fieldDefinition);
				break;
			case DATERANGE:
				this.fieldGenerator = new DateRangeGenerator(fieldDefinition);
				break;
		}
	}

	public void initCellStyle(Workbook wb) {
		FieldType type = fieldDefinition.getType();
		if (FieldType.DATE.equals(type)) {
			DataFormat dataFormat = wb.createDataFormat();
			cellStyle = wb.createCellStyle();
			cellStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy"));
		}
	}

	public CellStyle getCellStyle() {
		return cellStyle;
	}
}
