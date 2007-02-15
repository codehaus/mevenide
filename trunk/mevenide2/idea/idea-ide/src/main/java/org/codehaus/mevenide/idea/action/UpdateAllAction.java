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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;

import org.codehaus.mevenide.idea.PluginConfigurationManager;
import org.codehaus.mevenide.idea.PluginLoggerManager;
import org.codehaus.mevenide.idea.PluginPomManager;
import org.codehaus.mevenide.idea.console.PluginLogger;

/**
 * An action that re-searches all of the modules for Maven 2 POM files and updates the project.
 *
 * @author bkate
 */
public class UpdateAllAction extends AnAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(UpdateAction.class);

        logger.debug("Updating project from Maven POMs");
        PluginPomManager.getInstance(project).updateProjectModules();
    }

    /** {@inheritDoc} */
    public void update(AnActionEvent e) {
        super.update(e);

        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);

        if (project == null) {
            e.getPresentation().setEnabled(false);
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setEnabled(
                PluginConfigurationManager.getInstance(project).getConfig().isPluginEnabled());
            e.getPresentation().setVisible(true);
        }
    }
}
