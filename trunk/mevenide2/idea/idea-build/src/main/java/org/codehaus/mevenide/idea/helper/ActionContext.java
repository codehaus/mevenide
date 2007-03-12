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
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.config.IdeaMavenPluginDocument;
import org.codehaus.mevenide.idea.config.PluginConfigDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

    /**
     * Constructs ...
     */
    public ActionContext() {
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

    /**
     * Method description
     *
     * @return Document me!
     */
    public PluginConfigDocument.PluginConfig getProjectPluginConfiguration() {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            Map<String, String> xmlOptionsMap = new Hashtable<String, String>();

            xmlOptionsMap.put("", "org/apache/maven/plugin");
            xmlOptions.setLoadSubstituteNamespaces(xmlOptionsMap);

            IdeaMavenPluginDocument ideaMavenPluginDoc = IdeaMavenPluginDocument.Factory.parse(
                    this.getClass().getResource(
                            PluginConstants.PLUGIN_CONFIG_FILENAME), xmlOptions);

            return ideaMavenPluginDoc.getIdeaMavenPlugin().getPluginConfig();
        } catch (Exception e) {
            ErrorHandler.processAndShowError(getPluginProject(), e);
        }

        return null;
    }
}
