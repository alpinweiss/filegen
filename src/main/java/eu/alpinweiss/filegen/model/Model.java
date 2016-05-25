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
package eu.alpinweiss.filegen.model;

import eu.alpinweiss.filegen.command.CommandStep;

import java.util.*;

/**
 * {@link Model}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 * 
 */
public class Model {

    private List<FieldDefinition> fieldDefinitionList = new ArrayList<>();

	private final Map<String, String> parametersMap = new HashMap<>();
	private final Set<CommandStep> commands = new LinkedHashSet<>();
    private long rowCount;
    private String lineSeparator;
    private String outputFileName;
	private int dataStorageCount;

	public Map<String, String> getParametersMap() {
		return parametersMap;
	}

	public void addParameter(String key, String value) {
		this.parametersMap.put(key, value);
	}

	public String getParameter(String key) {
		return this.parametersMap.get(key);
	}

	public Set<CommandStep> getCommands() {
		return commands;
	}

	public void addCommand(CommandStep command) {
		this.commands.add(command);
	}

    public List<FieldDefinition> getFieldDefinitionList() {
        return fieldDefinitionList;
    }

    public void setFieldDefinitionList(List<FieldDefinition> fieldDefinitionList) {
        this.fieldDefinitionList = fieldDefinitionList;
    }

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

	public void setDataStorageCount(int dataStorageCount) {
		this.dataStorageCount = dataStorageCount;
	}

	public int getDataStorageCount() {
		return dataStorageCount;
	}
}
