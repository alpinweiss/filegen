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
package eu.alpinweiss.filegen.util.writer;

/**
 * {@link DefaultOutputWriter}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class DefaultOutputWriter implements OutputWriter {

	@Override
	public void writeValueInLine(String value) {
		System.out.println(value);
	}

	@Override
	public void writeValue(String value) {
		System.out.print(value);
	}

	@Override
	public void writeException(Exception e) {
		if (e != null) {
			e.printStackTrace(System.out);
		} else {
			System.out.println("Exception is null!");
		}
	}
}
