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
 * An action that updates the classpath of the module containing the POM file selected.
 *
 * @author bkate
 */
public class UpdateAction extends MavenGroupAction {

    /** {@inheritDoc} */
    public void actionPerformed(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        VirtualFile pomFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        PluginLogger logger = PluginLoggerManager.getInstance(project).getPluginLogger(UpdateAction.class);

        logger.debug("Updating project for POM: " + pomFile.getPath());
        PluginPomManager.getInstance(project).updateProjectModules();
    }
}
