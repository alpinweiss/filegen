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
package eu.alpinweiss.filegen.config;

import com.google.inject.AbstractModule;
import eu.alpinweiss.filegen.command.runner.CommandRunner;
import eu.alpinweiss.filegen.command.runner.CommandRunnerImpl;
import eu.alpinweiss.filegen.command.steps.*;
import eu.alpinweiss.filegen.command.steps.impl.*;
import eu.alpinweiss.filegen.service.*;
import eu.alpinweiss.filegen.service.impl.*;

/**
 * {@link FdrModule}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 * 
 */
public class FdrModule extends AbstractModule {
	@Override
	protected void configure() {
        bind(CommandRunner.class).to(CommandRunnerImpl.class);
        bind(AppVersionStep.class).to(AppVersionStepImpl.class);
        bind(GenerateAdvancedFileStep.class).to(GenerateAdvancedFileStepImpl.class);
        bind(GenerateExcelXlsxFileStep.class).to(GenerateExcelXlsxFileStepImpl.class);
        bind(GenerateSimpleFileStep.class).to(GenerateSimpleFileStepImpl.class);
        bind(HelpStep.class).to(HelpStepImpl.class);
        bind(FdrOptionHolder.class).to(FdrOptionHolderImpl.class);
        bind(FdrService.class).to(FdrServiceImpl.class);
        bind(GenerateAdvancedFileService.class).to(GenerateAdvancedFileServiceImpl.class);
        bind(GenerateXlsxFileService.class).to(GenerateXlsxFileServiceImpl.class);
        bind(XmlConfigParser.class).to(XmlConfigParserImpl.class);
        bind(ReadInputParametersStep.class).to(ReadInputParametersStepImpl.class);
        bind(OutputWriterHolder.class).to(OutputWriterHolderImpl.class);
	}

}
