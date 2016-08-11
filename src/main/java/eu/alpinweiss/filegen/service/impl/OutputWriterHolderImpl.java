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
package eu.alpinweiss.filegen.service.impl;

import com.google.inject.Singleton;
import eu.alpinweiss.filegen.service.OutputWriterHolder;
import eu.alpinweiss.filegen.util.writer.DefaultOutputWriter;
import eu.alpinweiss.filegen.util.writer.OutputWriter;

/**
 * {@link OutputWriterHolderImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
@Singleton
public class OutputWriterHolderImpl implements OutputWriterHolder {

	private OutputWriter outputWriter;

	public OutputWriterHolderImpl() {
		this.outputWriter = new DefaultOutputWriter();
	}

	@Override
	public void setOutputWriter(OutputWriter outputWriter) {
		this.outputWriter = outputWriter;
	}

	@Override
	public void writeValueInLine(String value) {
		outputWriter.writeValueInLine(value);
	}

	@Override
	public void writeValue(String value) {
		outputWriter.writeValue(value);
	}
}
