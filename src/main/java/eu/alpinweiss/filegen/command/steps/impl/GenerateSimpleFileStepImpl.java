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
package eu.alpinweiss.filegen.command.steps.impl;

import eu.alpinweiss.filegen.command.steps.GenerateSimpleFileStep;
import eu.alpinweiss.filegen.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * {@link GenerateSimpleFileStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class GenerateSimpleFileStepImpl implements GenerateSimpleFileStep {

    private final static Logger LOGGER = LogManager.getLogger(GenerateSimpleFileStepImpl.class);

    @Override
    public void execute(Model model) {

        long iterations = getIterations(model.getParameter(SIMPLE_FILE));

        long startTime = new Date().getTime();

        try {
            String str = "SomeMoooreText\r\n";
            File newTextFile = new File("thetextfile.txt");

            FileWriter fw = new FileWriter(newTextFile);

            for (long i = 0; i < iterations ; i++) {
                if (i != 0 && i % 1000000 == 0) {
                    System.out.println("processed " + i + " rows");
                }
                fw.write(str);
            }
            fw.close();

            System.out.println("Done");
            System.out.println("Time used " + ((new Date().getTime() - startTime) / 1000) + " sec");
        } catch (IOException iox) {
            LOGGER.error(iox.getMessage(), iox);
        }

    }

    private long getIterations(String arg) {
        if (arg!= null) {
            return Long.parseLong(arg);
        }
        return 250000000;
    }
}
