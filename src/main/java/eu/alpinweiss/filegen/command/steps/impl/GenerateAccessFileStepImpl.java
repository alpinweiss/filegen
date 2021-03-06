/*
 * Copyright 2016 Alexander Severgin
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

import com.google.inject.Inject;
import eu.alpinweiss.filegen.command.steps.GenerateAccessFileStep;
import eu.alpinweiss.filegen.model.Model;
import eu.alpinweiss.filegen.service.GenerateAccessFileService;

/**
 * {@link GenerateAccessFileStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class GenerateAccessFileStepImpl implements GenerateAccessFileStep {

	@Inject
	private GenerateAccessFileService generateAccessFileService;

	@Override
	public void execute(Model model) {
		String outputFileName = model.getOutputFileName();
		generateAccessFileService.generateAccess(outputFileName, model.getRowCount(), model.getFieldDefinitionList(), model.getDataStorageCount());
		model.getFieldDefinitionList().clear();
	}

}
