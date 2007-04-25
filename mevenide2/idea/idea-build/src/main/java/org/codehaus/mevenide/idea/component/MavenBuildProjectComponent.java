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


package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.CorePlugin;
import org.codehaus.mevenide.idea.IMevenideIdeaComponent;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;
import org.codehaus.mevenide.idea.gui.form.MavenBuildConfigurationForm;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.helper.GuiContext;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.SettingsDocument;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.Map;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildProjectComponent
        implements IMevenideIdeaComponent, ProjectComponent, JDOMExternalizable {
    private static final Logger LOG = Logger.getLogger(MavenBuildProjectComponent.class);

    public static MavenBuildProjectComponent getInstance(Project project) {
        return project.getComponent(MavenBuildProjectComponent.class);
    }

    private GuiContext guiContext = new GuiContext();
    private BuildContext buildContext;
    public PomTreeStructure pomTreeStructure;
    private MavenBuildPluginSettings projectPluginSettings = new MavenBuildPluginSettings();

    private Project project;

    public PomTreeStructure getPomTreeStructure() {
        return pomTreeStructure;
    }

    public BuildContext getBuildContext() {
        return buildContext;
    }

    public GuiContext getGuiContext() {
        return guiContext;
    }

    public void setBuildContext(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public MavenBuildPluginSettings getProjectPluginSettings() {
        return projectPluginSettings;
    }

    public MavenBuildProjectComponent(Project project, CorePlugin corePlugin) {
        this.project = project;
        LOG.info("Trying to register Mevenide component <" + getComponentName() + "> into Mevenide Core");
        corePlugin.registerMevenideComponent(this);

        projectPluginSettings.setMavenConfiguration(corePlugin.getMavenConfiguration());
        projectPluginSettings.setMavenRepository(findMavenRepository());

        pomTreeStructure = new PomTreeStructure(project, projectPluginSettings);
    }

    public JComponent createComponent() {
        return guiContext.getProjectConfigurationForm().getRootComponent();
    }

    public void disposeComponent() {
        // empty
    }

    public void disposeUIResources() {
        guiContext.setProjectConfigurationForm(null);
    }

    private MavenBuildConfigurationForm getProjectConfigurationForm() {
        return (MavenBuildConfigurationForm) guiContext.getProjectConfigurationForm();
    }

    public void initComponent() {
        LOG.debug("Location of Maven Repository is: " + projectPluginSettings.getMavenRepository());
        guiContext.setMavenToolWindowForm(createMavenToolWindowForm());
        guiContext.setProjectConfigurationForm(createMavenBuildConfigurationForm());
    }

    private MavenBuildProjectToolWindowForm createMavenToolWindowForm() {
        return new MavenBuildProjectToolWindowForm(project, projectPluginSettings);
    }

    private MavenBuildConfigurationForm createMavenBuildConfigurationForm() {
        return new MavenBuildConfigurationForm();
    }

    public void projectOpened() {
        initToolWindow();
        new PomWatcher(project, pomTreeStructure);
    }

    public void projectClosed() {
        unregisterToolWindow();
    }

    private void initToolWindow() {
        MavenBuildProjectToolWindowForm form = (MavenBuildProjectToolWindowForm) guiContext.getMavenToolWindowForm();
        form.getScrollpane().setViewportView(pomTreeStructure.getTree());

        ToolWindow pomToolWindow = ToolWindowManager.getInstance(project).registerToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID,
                form.getRootComponent(), ToolWindowAnchor.RIGHT);

        pomToolWindow.setIcon(IconLoader.getIcon(PluginConstants.ICON_APPLICATION_EMBLEM_SMALL));
    }

    private void unregisterToolWindow() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID);
    }

    private static String findMavenRepository() {
        String mavenHomeDir = System.getProperty("user.home") + System.getProperty("file.separator") + ".m2";
        File settingsFile = new File(mavenHomeDir + System.getProperty("file.separator") + "settings.xml");

        if (settingsFile.exists()) {
            try {
                SettingsDocument settingsDocument = SettingsDocument.Factory.parse(settingsFile);
                if (settingsDocument != null) {
                    if (!StringUtils.isEmpty(settingsDocument.getSettings().getLocalRepository())) {
                        return settingsDocument.getSettings().getLocalRepository();
                    }
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
        return mavenHomeDir + System.getProperty("file.separator") + "repository";
    }

    public void readExternal(Element element) throws InvalidDataException {

        projectPluginSettings.setMavenHome(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE));
        projectPluginSettings.setMavenCommandLineParams(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE));
        projectPluginSettings.setVmOptions(JDOMExternalizerUtil.readField(element, PluginConstants.CONFIG_ELEMENT_VM_OPTIONS));
        projectPluginSettings.setUseMavenEmbedder(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER)));
        projectPluginSettings.setRunMavenInBackground(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_RUN_MAVEN_IN_BACKGROUND)));
        projectPluginSettings.setSkipTests(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_SKIP_TESTS)));
        projectPluginSettings.setJdkPath(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_JDK_PATH));

        Element mavenPropertiesElement = element.getChild("maven-properties");
        if (mavenPropertiesElement != null) {
            for (Object child : mavenPropertiesElement.getChildren()) {
                Element childElement = (Element) child;
                if (childElement != null) {
                    String key = childElement.getAttributeValue("name");
                    String value = childElement.getAttributeValue("value");
                    projectPluginSettings.getMavenProperties().put(key, value);
                }
            }
        }

        pomTreeStructure.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException {

        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE,
                projectPluginSettings.getMavenHome());
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_SETTINGS_FILE,
                projectPluginSettings.getMavenSettingsFile());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE,
                projectPluginSettings.getMavenCommandLineParams());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_VM_OPTIONS,
                projectPluginSettings.getVmOptions());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER,
                Boolean.toString(projectPluginSettings.isUseMavenEmbedder()));
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_RUN_MAVEN_IN_BACKGROUND,
                Boolean.toString(projectPluginSettings.isRunMavenInBackground()));
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_SKIP_TESTS,
                Boolean.toString(projectPluginSettings.isSkipTests()));
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_JDK_PATH,
                projectPluginSettings.getJdkPath());

        Element mavenPropertiesElement = new Element("maven-properties");
        element.addContent(mavenPropertiesElement);
        for ( Map.Entry<String,String> entry : projectPluginSettings.getMavenProperties().entrySet()){
            JDOMExternalizerUtil.writeField(mavenPropertiesElement, entry.getKey(), entry.getValue());
        }

        pomTreeStructure.writeExternal(element);
    }

    public void apply() throws ConfigurationException {
        MavenBuildConfigurationForm form = getProjectConfigurationForm();
        if (form != null) {
            form.getData(projectPluginSettings);
        }
    }

    public void reset() {
        MavenBuildConfigurationForm form = getProjectConfigurationForm();
        if (form != null) {
            form.setData(projectPluginSettings);
        }
    }

    @NotNull
    public String getComponentName() {
        return PluginConstants.PROJECT_COMPONENT_NAME;
    }

    @Nls
    public String getDisplayName() {
        return PluginConstants.PLUGIN_PROJECT_DISPLAY_NAME;
    }

    public boolean isModified() {
        MavenBuildConfigurationForm form = getProjectConfigurationForm();
        return (form != null) && form.isModified(projectPluginSettings);
    }

    public JComponent getMevenideConfigurationComponent() {
        return createComponent();
    }

    public String getMevenideComponentName() {
        return getDisplayName();
    }

    public boolean isMevenideConfigurationModified() {
        return isModified();
    }

    public void applyMevenideConfiguration() {
        try {
            apply();
        } catch (ConfigurationException e) {
            LOG.error(e);
        }
    }

    public void resetMevenideConfiguration() {
        reset();
    }
}
