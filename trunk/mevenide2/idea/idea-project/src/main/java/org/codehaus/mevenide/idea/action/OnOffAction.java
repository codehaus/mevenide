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

package org.codehaus.mevenide.idea.action;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;

import org.codehaus.mevenide.idea.PluginPomManager;
import org.codehaus.mevenide.idea.PluginLoggerManager;
import org.codehaus.mevenide.idea.console.PluginLogger;


/**
 * An action that toggles the parsing of the selected POM file on/off.
 *
 * @author bkate
 */
public class OnOffAction extends MavenGroupAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {

        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);

        PluginPomManager mgr = PluginPomManager.getInstance(project);
        VirtualFile pomFile = (VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(OnOffAction.class);

        mgr.setPomEnabled(pomFile, !mgr.isPomEnabled(pomFile));
        mgr.updateProjectModules();

        logger.debug("Toggling for POM: " + pomFile.getPath());
    }


    /**
     * {@inheritDoc}
     *
     * Uses the PluginPomManager to determine the text that should be displayed.
     */
    public void update(AnActionEvent e) {

        super.update(e);

        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);

        String actionText = "Disable";

        if (!PluginPomManager.getInstance(project).isPomEnabled((VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE))) {
            actionText = "Enable";
        }

        e.getPresentation().setText(actionText);
    }


    /** {@inheritDoc} */
    protected boolean isActionEnabled(AnActionEvent e) {
        return isPomFile((VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE));
    }

}

