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
import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.mevenide.idea.PluginLoggerManager;
import org.codehaus.mevenide.idea.PluginPomManager;
import org.codehaus.mevenide.idea.console.PluginLogger;

/**
 * An action that toggles the parsing of the selected POM file on/off.
 *
 * @author bkate
 */
public class OnOffAction extends MavenGroupAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        PluginPomManager mgr = PluginPomManager.getInstance(project);
        VirtualFile pomFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
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

        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        String actionText = "Disable";

        if (!PluginPomManager.getInstance(project).isPomEnabled(
                (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE))) {
            actionText = "Enable";
        }

        e.getPresentation().setText(actionText);
    }

    /** {@inheritDoc} */
    protected boolean isActionEnabled(AnActionEvent e) {
        return isPomFile((VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE));
    }
}
