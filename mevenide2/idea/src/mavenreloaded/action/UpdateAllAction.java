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


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import mavenreloaded.PluginPomManager;
import mavenreloaded.PluginLoggerManager;
import mavenreloaded.console.PluginLogger;


/**
 * An action that re-searches all of the modules for Maven 2 POM files and updates the project.
 *
 * @author bkate
 */
public class UpdateAllAction extends AnAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {

        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(UpdateAction.class);

        logger.debug("Updating project from Maven POMs");

        PluginPomManager.getInstance(project).updateProjectModules();
    }


    /** {@inheritDoc} */
    public void update(AnActionEvent e) {

        super.update(e);

        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);

        if (project == null) {
            e.getPresentation().setEnabled(false);
        }
        else {
            e.getPresentation().setEnabled(true);
        }
    }

}