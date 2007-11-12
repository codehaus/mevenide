/* ==========================================================================
 * Copyright 2006 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */



package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import org.apache.maven.execution.ReactorManager;

import org.codehaus.mevenide.idea.PluginLoggerManager;
import org.codehaus.mevenide.idea.PluginPomManager;
import org.codehaus.mevenide.idea.console.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An action that brings up a dialog to execute a goal on the selected POM file.
 *
 * @author bkate
 */
public class ExecuteAction extends MavenGroupAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {
        VirtualFile pomFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(ExecuteAction.class);

        logger.debug("User-initiated execution of POM: " + pomFile.getPath());

        // get input from the user
        String input = Messages.showInputDialog(project, "Enter the Maven command to execute.", "Maven POM Execution",
                           Messages.getQuestionIcon(), "clean compile", new InputValidator() {

            /** {@inheritDoc} */
            public boolean checkInput(String inputString) {
                return true;
            }

            /** {@inheritDoc} */
            public boolean canClose(String inputString) {
                return (inputString.length() > 0);
            }
        });

        // make sure that input was given
        if (input == null) {
            logger.debug("Execution action cancelled.");

            return;
        }

        List<String> goals = new ArrayList<String>();
        Properties props = new Properties();

        for (String part : input.trim().split("\\s")) {

            // check to see if it is a property or goal
            if (part.startsWith("-D")) {
                String key = part.substring(2);
                String value = "";

                // property has optional value
                int equalsIndex = part.indexOf("=");

                if (equalsIndex >= 0) {
                    key = part.substring(2, equalsIndex);
                    value = part.substring(equalsIndex + 1);
                }

                props.setProperty(key, value);
            } else {
                goals.add(part);
            }
        }

        // execute the goals
        PluginPomManager.getInstance(project).executeGoals(pomFile, goals, props, true, ReactorManager.FAIL_FAST);
    }
}
