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

import eu.alpinweiss.filegen.command.steps.AppVersionStep;
import eu.alpinweiss.filegen.model.Model;

/**
 * {@link AppVersionStepImpl}.
 *
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 */
public class AppVersionStepImpl implements AppVersionStep {

    public static final String VERSION = "1";

    @Override
    public void execute(Model model) {
        System.out.println("filegen  v" + VERSION);
    }
}
