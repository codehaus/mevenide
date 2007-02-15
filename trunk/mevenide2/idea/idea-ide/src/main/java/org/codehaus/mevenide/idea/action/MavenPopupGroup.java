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
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.mevenide.idea.MavenConstants;
import org.codehaus.mevenide.idea.PluginConfigurationManager;

/**
 * A group that represents all of the actions that are related to Maven 2 processing.
 *
 * @author bkate
 */
public class MavenPopupGroup extends DefaultActionGroup {

    /**
     * Default constructor.
     */
    public MavenPopupGroup() {
        super();
    }

    /**
     * More involved constructor.
     *
     * @param shortName The short name of the group.
     * @param popup True if the group is a popup group.
     */
    public MavenPopupGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }

    /**
     * {@inheritDoc}
     *
     * Turns the group off if it is not a Maven 2 POM file.
     */
    public void update(AnActionEvent e) {
        super.update(e);

        boolean enabled = isGroupEnabled(e);

        e.getPresentation().setEnabled(enabled);
    }

    /**
     * Determines if the group is being called on a Maven 2 POM file.
     *
     * @param file The file being acted on in this group.
     *
     * @return True if it is a POM file, false otherwise.
     */
    protected boolean isPomFile(VirtualFile file) {

        // only enable the action for pom files
        if (file == null) {
            return false;
        }

        return file.getName().equalsIgnoreCase(MavenConstants.POM_NAME);
    }

    /**
     * Determines if this action group should be enabled.
     *
     * @param e The action event triggering this call.
     *
     * @return True if the event context supports enabling this action group.
     */
    protected boolean isGroupEnabled(AnActionEvent e) {
        VirtualFile thisFile = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        PluginConfigurationManager config = PluginConfigurationManager.getInstance(project);

        return config.getConfig().isPluginEnabled() && isPomFile(thisFile);
    }
}
