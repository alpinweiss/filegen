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
package eu.alpinweiss.filegen.service;

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.model.Model;

import java.util.List;

/**
 * {@link GenerateXlsxFileService}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 *
 */
public interface GenerateXlsxFileService {

	void generateExcel(String excelFilename, long rowCount, List<FieldDefinition> fieldDefinitionList, int sheetCount);

}
