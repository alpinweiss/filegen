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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.alpinweiss.filegen.command.CommandStep;
import eu.alpinweiss.filegen.service.XmlConfigParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link FdrOptionHolderImpl}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
@Singleton
public class FdrOptionHolderImpl implements FdrOptionHolder {

	private final Map<String, Class<? extends CommandStep>> commandSteps = new LinkedHashMap<String, Class<? extends CommandStep>>();
	private final Options options = new Options();

	@Inject
	public FdrOptionHolderImpl(XmlConfigParser xmlConfigParser) {
		Set<FdrStep> fdrSteps = xmlConfigParser.getFdrSteps("commands.xml");
		initOptions(fdrSteps);
	}

	@SuppressWarnings("static-access")
	private void initOptions(Set<FdrStep> fdrSteps) {
		for (FdrStep fdrStep : fdrSteps) {
            options.addOption(OptionBuilder.hasArgs(1).hasOptionalArgs(1)
                    .isRequired(fdrStep.getRequired())
                    .withValueSeparator(' ')
                    .withDescription(fdrStep.getDescription())
                    .create(fdrStep.getCommand()));
//			options.addOption(fdrStep.getCommand(), fdrStep.getRequired(), fdrStep.getDescription());
			commandSteps.put(fdrStep.getCommand(), getClassByClassName(fdrStep));
		}
	}

	@SuppressWarnings("unchecked")
	private Class<CommandStep> getClassByClassName(FdrStep fdrStep) {
		try {
			return (Class<CommandStep>) Class.forName(fdrStep.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Options getAppOptions() {
		return options;
	}

	@Override
	public Map<String, Class<? extends CommandStep>> getCommandStepMap() {
		return commandSteps;
	}

}
