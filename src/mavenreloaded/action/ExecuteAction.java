/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mavenreloaded.action;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.InputValidator;

import mavenreloaded.console.PluginLogger;
import mavenreloaded.PluginLoggerManager;
import mavenreloaded.PluginPomManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.maven.execution.ReactorManager;


/**
 * An action that brings up a dialog to execute a goal on the selected POM file.
 *
 * @author bkate
 */
public class ExecuteAction extends MavenGroupAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {

        VirtualFile pomFile = (VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(ExecuteAction.class);

        logger.debug("User-initiated execution of POM: " + pomFile.getPath());

        // get input from the user
        String input = Messages.showInputDialog(project,
                                                "Enter the Maven command to execute.",
                                                "Maven POM Execution",
                                                Messages.getQuestionIcon(),
                                                "clean compile",
                                                new InputValidator() {

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
            }
            else {
                goals.add(part);
            }
        }

        // execute the goals
        PluginPomManager.getInstance(project).executeGoals(pomFile, goals, props, true, ReactorManager.FAIL_FAST);
    }

}

