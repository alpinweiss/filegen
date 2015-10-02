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

/**
 * {@link FdrStep}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 * 
 */
public class FdrStep {

	private String command;
	private String className;
	private String description;
	private Boolean required;
    private Integer parameters;
    private Integer requiredParameters;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

    public Integer getParameters() {
        return parameters;
    }

    public void setParameters(Integer parameters) {
        this.parameters = parameters;
    }

    public Integer getRequiredParameters() {
        return requiredParameters;
    }

    public void setRequiredParameters(Integer requiredParameters) {
        this.requiredParameters = requiredParameters;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		FdrStep fdrFile = (FdrStep) o;

		if (command == null || !command.equals(fdrFile.getCommand()))
			return false;
		if (description == null || !description.equals(fdrFile.getDescription()))
			return false;
		if (className == null || !className.equals(fdrFile.getClassName()))
			return false;
		if (required == null || !required.equals(fdrFile.getRequired()))
			return false;
        if (parameters == null || !parameters.equals(fdrFile.getParameters()))
			return false;
        if (requiredParameters == null || !requiredParameters.equals(fdrFile.getRequiredParameters()))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 31 + (command == null ? 0 : command.hashCode());
		result = result * 31 + (className == null ? 0 : className.hashCode());
		result = result * 31 + (description == null ? 0 : description.hashCode());
		result = result * 31 + (required == null ? 0 : required.hashCode());
		result = result * 31 + (parameters == null ? 0 : parameters.hashCode());
		result = result * 31 + (requiredParameters == null ? 0 : requiredParameters.hashCode());
		return result;
	}

}
