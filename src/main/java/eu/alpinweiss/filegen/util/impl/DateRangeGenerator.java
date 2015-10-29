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
package eu.alpinweiss.filegen.util.impl;

import eu.alpinweiss.filegen.model.FieldDefinition;
import eu.alpinweiss.filegen.util.FieldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link DateRangeGenerator}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class DateRangeGenerator implements FieldGenerator {

	private final FieldDefinition fieldDefinition;
	private SimpleDateFormat dateFormat;

	private final static Logger LOGGER = LogManager.getLogger(DateRangeGenerator.class);

	public DateRangeGenerator(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public void generate(int iterationNo, ThreadLocalRandom randomGenerator, Cell cell) {
		synchronized (this) {
			String pattern = fieldDefinition.getPattern();
			if (pattern == null || "".equals(pattern)) {
				cell.setCellValue("");
				return;
			}

			String[] split = pattern.split(":");
			if (split.length == 1) {
				cell.setCellValue(pattern);
				return;
			}
			try {
				String dateFormatPattern = split[0];
				dateFormat = new SimpleDateFormat(dateFormatPattern);
				Date min = dateFormat.parse(split[1]);
				Date max = dateFormat.parse(split[2]);

				List<Date> dates = new ArrayList<>();

				Calendar c = Calendar.getInstance();
				int counter = 1;
				while (true) {
					c.setTime(min);
					c.add(Calendar.DATE, counter++);
					Date date = c.getTime();
					dates.add(date);
					if (date.equals(max)) {
						break;
					}
				}

				int index = ThreadLocalRandom.current().nextInt(0, dates.size());
				cell.setCellValue(dateFormat.format(dates.get(index)));
			} catch (ParseException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
