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


package org.codehaus.mevenide.idea.helper;

import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.MavenDefaultsDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class ActionContext {
    private GuiContext guiContext = new GuiContext();
    private String lastExecutedMavenProject;
    private MavenBuildPluginSettings projectPluginSettings = new MavenBuildPluginSettings();
    private List<MavenProjectDocument> pomDocumentList = new ArrayList<MavenProjectDocument>();
    private Project pluginProject;
    MavenDefaultsDocument defaultsDocument;

    /**
     * Constructs ...
     */
    public ActionContext() {
        defaultsDocument = MavenDefaultsDocument.Factory.parse(
                getClass().getResource(PluginConstants.PLUGIN_CONFIG_FILENAME));
    }

    public Project getPluginProject() {
        return pluginProject;
    }

    public void setPluginProject(Project pluginProject) {
        this.pluginProject = pluginProject;
    }

    public List<MavenProjectDocument> getPomDocumentList() {
        return pomDocumentList;
    }

    public void setPomDocumentList(List<MavenProjectDocument> pomDocumentList) {
        this.pomDocumentList = pomDocumentList;
    }

    public MavenBuildPluginSettings getProjectPluginSettings() {
        return projectPluginSettings;
    }

    public void setProjectPluginSettings(MavenBuildPluginSettings projectPluginSettings) {
        this.projectPluginSettings = projectPluginSettings;
    }

    public GuiContext getGuiContext() {
        return guiContext;
    }

    public String getLastExecutedMavenProject() {
        return lastExecutedMavenProject;
    }

    public void setLastExecutedMavenProject(String lastExecutedMavenProject) {
        this.lastExecutedMavenProject = lastExecutedMavenProject;
    }

    public List<MavenDefaultsDocument.Goal> getStandardGoals() {
        return defaultsDocument.getIdeaMavenPlugin().getPluginConfig().getMaven().getGoals().getStandard().getGoalList();
    }
}
