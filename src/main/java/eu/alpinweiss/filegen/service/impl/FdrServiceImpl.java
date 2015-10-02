/*
 * Copyright 2011 Alexander Severgin
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

package eu.alpinweiss.filegen.service.impl;

import java.util.Map;

import eu.alpinweiss.filegen.command.CommandStep;
import eu.alpinweiss.filegen.command.runner.CommandRunner;
import eu.alpinweiss.filegen.config.FdrOptionHolder;
import eu.alpinweiss.filegen.model.Model;
import eu.alpinweiss.filegen.service.FdrService;
import org.apache.commons.cli.*;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * {@link FdrServiceImpl}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
@Singleton
public class FdrServiceImpl implements FdrService {

	@Inject
	private CommandRunner commandRunner;
	@Inject
	private FdrOptionHolder optionHolder;
	private Injector injector;

	@Override
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void run(String[] args) {

		if (injector == null) {
			throw new RuntimeException("Injector is null");
		}

		Model model = new Model();
		Map<String, Class<? extends CommandStep>> commandSteps = optionHolder.getCommandStepMap();
		Options options = optionHolder.getAppOptions();

		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
            Option[] cmdOptions = cmd.getOptions();
            for (Option cmdOption : cmdOptions) {
                model.addParameter(cmdOption.getOpt(), cmdOption.getValue());
                model.addCommand(injector.getInstance(commandSteps.get(cmdOption.getOpt())));
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}

		commandRunner.run(model);
	}

}
