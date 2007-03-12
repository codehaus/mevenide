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

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.settings.x100.SettingsDocument;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.mevenide.idea.CorePlugin;
import org.codehaus.mevenide.idea.IMevenideIdeaComponent;
import org.codehaus.mevenide.idea.action.ActionUtils;
import org.codehaus.mevenide.idea.action.AddPluginAction;
import org.codehaus.mevenide.idea.action.AddPomAction;
import org.codehaus.mevenide.idea.action.FilterAction;
import org.codehaus.mevenide.idea.action.PluginConfigurationActionListener;
import org.codehaus.mevenide.idea.action.PomTreeMouseActionListener;
import org.codehaus.mevenide.idea.action.RemovePluginAction;
import org.codehaus.mevenide.idea.action.RemovePomAction;
import org.codehaus.mevenide.idea.action.RunGoalsAction;
import org.codehaus.mevenide.idea.action.ShowMavenOptionsAction;
import org.codehaus.mevenide.idea.action.SortAction;
import org.codehaus.mevenide.idea.action.ToolWindowKeyListener;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.gui.form.MavenProjectConfigurationForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocumentImpl;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import java.awt.Dimension;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildProjectComponent extends AbstractComponent
        implements IMevenideIdeaComponent, ProjectComponent, JDOMExternalizable {
    private static final Logger LOG = Logger.getLogger(MavenBuildProjectComponent.class);
    private ActionContext actionContext = new ActionContext();
    private Set<MavenProjectDocument> mavenPomList = new LinkedHashSet<MavenProjectDocument>();

    /**
     * Constructs ...
     *
     * @param project Document me!
     */
    public MavenBuildProjectComponent(Project project, CorePlugin corePlugin) {
        actionContext.setPluginProject(project);
        LOG.info("Trying to register Mevenide component <" + getComponentName() + "> into Mevenide Core");
        corePlugin.registerMevenideComponent(this);
    }

    private MavenBuildProjectToolWindowForm createMavenToolWindowForm() {
        MavenBuildProjectToolWindowForm toolWindowForm = new MavenBuildProjectToolWindowForm();
        DefaultActionGroup group = new DefaultActionGroup();
        AnAction actionAddPom = new AddPomAction(actionContext, PluginConstants.ACTION_COMMAND_ADD_POM,
                "Adds a POM to the project", IconLoader.getIcon(PluginConstants.ICON_ADD_POM));
        AnAction actionRemovePom = new RemovePomAction(actionContext, PluginConstants.ACTION_COMMAND_REMOVE_POM,
                "Removes a POM from the project",
                IconLoader.getIcon(PluginConstants.ICON_REMOVE_POM));
        AnAction actionAddPlugin = new AddPluginAction(actionContext, PluginConstants.ACTION_COMMAND_ADD_PLUGIN,
                PluginConstants.ACTION_COMMAND_ADD_PLUGIN,
                IconLoader.getIcon(PluginConstants.ICON_ADD_PLUGIN));
        AnAction actionRemovePlugin = new RemovePluginAction(actionContext,
                PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN,
                PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN,
                IconLoader.getIcon(PluginConstants.ICON_REMOVE_PLUGIN));
        AnAction actionRunGoals = new RunGoalsAction(actionContext, PluginConstants.ACTION_COMMAND_RUN_GOALS,
                PluginConstants.ACTION_COMMAND_RUN_GOALS,
                IconLoader.getIcon(PluginConstants.ICON_RUN));
        AnAction actionSortAsc = new SortAction(actionContext, PluginConstants.ACTION_COMMAND_SORT_ASC,
                PluginConstants.ACTION_COMMAND_SORT_ASC,
                IconLoader.getIcon(PluginConstants.ICON_SORT_ASC));
        AnAction showMavenOptions = new ShowMavenOptionsAction(actionContext,
                PluginConstants.ACTION_COMMAND_SHOW_MAVEN_OPTIONS,
                PluginConstants.ACTION_COMMAND_SHOW_MAVEN_OPTIONS,
                IconLoader.getIcon(PluginConstants.ICON_SHOW_MAVEN_OPTIONS));
        AnAction filter = new FilterAction(actionContext, PluginConstants.ACTION_COMMAND_FILTER,
                PluginConstants.ACTION_COMMAND_FILTER,
                actionContext.getProjectPluginSettings().isUseFilter()
                        ? IconLoader.getIcon(PluginConstants.ICON_FILTER_APPLIED)
                        : IconLoader.getIcon(PluginConstants.ICON_FILTER));

        group.add(actionAddPom);
        group.add(actionRemovePom);
        group.addSeparator();
        group.add(actionAddPlugin);
        group.add(actionRemovePlugin);
        group.addSeparator();
        group.add(actionRunGoals);
        group.addSeparator();
        group.add(actionSortAsc);
        group.add(showMavenOptions);
        group.add(filter);
        group.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Maven Toolbar", group, true);

        actionToolbar.getComponent().setEnabled(false);
        toolWindowForm.getRootComponent().add(actionToolbar.getComponent(),
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));

        ToolWindowKeyListener keyListener = new ToolWindowKeyListener(actionContext);

        toolWindowForm.getTextFieldCmdLine().addKeyListener(keyListener);

        return toolWindowForm;
    }

    /**
     * Method description
     *
     * @throws ConfigurationException
     */
    public void apply() throws ConfigurationException {
        MavenProjectConfigurationForm form =
                (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

        if (form != null) {
            form.getData(pluginSettings);
        }
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public JComponent createComponent() {
        return actionContext.getGuiContext().getProjectConfigurationForm().getRootComponent();
    }

    private void createPomTree()
            throws org.apache.xmlbeans.XmlException, java.io.IOException, IdeaMavenPluginException {
        for (MavenProjectDocument mavenProjectDocument : mavenPomList) {
            ActionUtils.addSinglePomToTree(actionContext, mavenProjectDocument);

            // Register a document listener that listens for changes
        }
    }

    /**
     * Method description
     */
    public void disposeComponent() {

        // empty
    }

    /**
     * Method description
     */
    public void disposeUIResources() {

//      actionContext.getGuiContext().setProjectConfigurationForm(null);
    }

    /**
     * Method description
     */
    public void initComponent() {
        actionContext.getGuiContext().setMavenToolWindowForm(createMavenToolWindowForm());

        MavenProjectConfigurationForm form =
                (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();

        if (form == null) {
            form = new MavenProjectConfigurationForm();

            PluginConfigurationActionListener actionListener = new PluginConfigurationActionListener(form);

            form.getButtonMavenHomeDir().addActionListener(actionListener);
            form.getButtonAlternativeSettingsFile().addActionListener(actionListener);
            actionContext.getGuiContext().setProjectConfigurationForm(form);
        }
    }

    private void initToolWindow(JTree tree) {
        JComponent pomContentPanel;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(actionContext.getPluginProject());
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();
        MavenBuildProjectToolWindowForm form =
                (MavenBuildProjectToolWindowForm) actionContext.getGuiContext().getMavenToolWindowForm();

        form.getScrollpane().setViewportView(tree);
        form.getTextFieldCmdLine().setText(pluginSettings.getMavenCommandLineParams());
        pomContentPanel = form.getRootComponent();

        // createToolbar(mavenToolWindowForm);
        ToolWindow pomToolWindow = toolWindowManager.registerToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID,
                pomContentPanel, ToolWindowAnchor.RIGHT);

        pomToolWindow.setIcon(GuiUtils.createImageIcon(PluginConstants.ICON_APPLICATION_EMBLEM_SMALL));
    }

    /**
     * Method description
     */
    public void projectClosed() {
        unregisterToolWindow();
    }

    /**
     * Method description
     */
    public void projectOpened() {
        PomTree pomTree;
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(actionContext.getPluginProject());
        Icon goalIcon = GuiUtils.createImageIcon(PluginConstants.ICON_APPLICATION_SMALL);
        Icon pomIcon = IconLoader.getIcon(PluginConstants.ICON_POM_SMALL);

        pomTree = new PomTree(PluginConstants.TREE_ROOT_NODE_TITLE, pomIcon, goalIcon);
        pomTree.addMouseListener(new PomTreeMouseActionListener(actionContext));

        if ((actionContext.getProjectPluginSettings() != null)
                && actionContext.getProjectPluginSettings().isScanForExistingPoms()) {
            mavenPomList = getPomFilesOfProject(projectRootManager);
        }

        pomTree.setRootVisible(true);
        initToolWindow(pomTree);

        try {
            createPomTree();
        } catch (Exception e) {
            ErrorHandler.processAndShowError(actionContext.getPluginProject(), e);
        }
    }

    /**
     * Method description
     *
     * @param element Document me!
     * @throws InvalidDataException
     */
    public void readExternal(Element element) throws InvalidDataException {
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

        pluginSettings.setMavenHome(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE));
        pluginSettings.setMavenCommandLineParams(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE));
        pluginSettings.setVmOptions(JDOMExternalizerUtil.readField(element, PluginConstants.CONFIG_ELEMENT_VM_OPTIONS));
        pluginSettings.setUseMavenEmbedder(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER)));
        pluginSettings.setUseFilter(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_USE_FILTER)));
        pluginSettings.setScanForExistingPoms(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                PluginConstants.CONFIG_ELEMENT_SCAN_FOR_POMS)));
        super.readExternal(actionContext.getProjectPluginSettings(), element);

        String mavenHomeDir = System.getProperty("user.home") + System.getProperty("file.separator") + ".m2";
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        File settingsFile = new File(mavenHomeDir + System.getProperty("file.separator") + "settings.xml");

        if (settingsFile.exists()) {
            try {
                XmlOptions xmlOptions = new XmlOptions();
                Map<String, String> xmlOptionsMap = new Hashtable<String, String>();

                xmlOptionsMap.put("", "http://maven.apache.org/Settings/1.0.0");
                xmlOptions.setLoadSubstituteNamespaces(xmlOptionsMap);

                SettingsDocument settingsDocument = SettingsDocument.Factory.parse(new File(mavenHomeDir
                        + System.getProperty("file.separator")
                        + "settings.xml"), xmlOptions);

                if (settingsDocument != null) {
                    if (!StringUtils.isEmpty(settingsDocument.getSettings().getLocalRepository())) {
                        pluginSettings.setMavenRepository(settingsDocument.getSettings().getLocalRepository());
                    }
                }
            } catch (Exception e) {
                LOG.error(e);

//              throw new InvalidDataException(e.getCause());
            }
        } else {
            pluginSettings.setMavenRepository(mavenHomeDir + System.getProperty("file.separator") + "repository");
        }

        LOG.debug("Location of Maven Repository is: " + pluginSettings.getMavenRepository());

        Element pomListElement = element.getChild("pom-list");

        if (pomListElement != null) {
            List myPomListChildren = pomListElement.getChildren("pom");

            for (Object aPomListChildren : myPomListChildren) {
                Element childElement = (Element) aPomListChildren;

                if (childElement != null) {
                    Element pomOptionElement = childElement.getChild("option");
                    String pomPath = pomOptionElement.getAttributeValue("value");
                    VirtualFile virtualFile = localFileSystem.findFileByPath(pomPath);

                    if (virtualFile != null) {
                        MavenProjectDocument mavenProjectDocument = new MavenProjectDocumentImpl(virtualFile);

                        mavenPomList.add(mavenProjectDocument);
                        LOG.debug("Adding POM: " + virtualFile.getPath());

                        Element pluginListChildren = childElement.getChild("plugin-list");

                        if (pluginListChildren != null) {
                            List myPluginListChildren = pluginListChildren.getChildren("option");

                            for (Object pluginListElement : myPluginListChildren) {
                                Element pluginChildElement = (Element) pluginListElement;
                                String pluginPath = pluginChildElement.getAttributeValue("value");
                                VirtualFile pluginJarArchive = localFileSystem.findFileByPath(pluginPath);

                                try {
                                    MavenPluginDocument mavenPluginDocument =
                                            ActionUtils.createMavenPluginDocument(pluginJarArchive, true);

                                    if (mavenPluginDocument != null) {
                                        mavenProjectDocument.getPluginDocumentList().add(mavenPluginDocument);
                                    }
                                } catch (Exception e) {
                                    LOG.error(e);

                                    throw new InvalidDataException(e.getCause());
                                }

                                LOG.debug("Adding Plugin: " + pluginPath);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method description
     */
    public void reset() {
        MavenProjectConfigurationForm form =
                (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

        if (form != null) {
            form.setData(pluginSettings);
        }
    }

    private void unregisterToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(actionContext.getPluginProject());

        toolWindowManager.unregisterToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID);
    }

    /**
     * Method description
     *
     * @param element Document me!
     * @throws WriteExternalException
     */
    public void writeExternal(Element element) throws WriteExternalException {
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE,
                pluginSettings.getMavenHome());
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_SETTINGS_FILE,
                pluginSettings.getMavenSettingsFile());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE,
                pluginSettings.getMavenCommandLineParams());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_VM_OPTIONS,
                pluginSettings.getVmOptions());
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER,
                Boolean.toString(pluginSettings.isUseMavenEmbedder()));
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_USE_FILTER,
                Boolean.toString(pluginSettings.isUseFilter()));
        JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_SCAN_FOR_POMS,
                Boolean.toString(pluginSettings.isScanForExistingPoms()));
        super.writeExternal(pluginSettings, element);

        Element pomListElement = new Element("pom-list");

        element.addContent(pomListElement);

        List<MavenProjectDocument> pomDocumentList = actionContext.getPomDocumentList();

        for (MavenProjectDocument pomFile : pomDocumentList) {
            Element pomElement = new Element("pom");

            pomListElement.addContent(pomElement);
            JDOMExternalizerUtil.writeField(pomElement, "path", pomFile.getPomFile().getPath());

            Element pluginListElement = new Element("plugin-list");

            pomElement.addContent(pluginListElement);

            List<MavenPluginDocument> pluginDocList = pomFile.getPluginDocumentList();

            for (MavenPluginDocument pluginDoc : pluginDocList) {

                // Store only those plugins, which were manually added by the user
                if (!pluginDoc.isMemberOfPom()) {
                    JDOMExternalizerUtil.writeField(pluginListElement, "path", pluginDoc.getPluginPath());
                }
            }
        }
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    @NotNull
    public String getComponentName() {
        return PluginConstants.PROJECT_COMPONENT_NAME;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    @Nls
    public String getDisplayName() {
        return PluginConstants
                .PLUGIN_PROJECT_DISPLAY_NAME;    // To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Method description
     *
     * @return Document me!
     */
/*
    @Nullable @NonNls
    public String getHelpTopic() {
        return null;
    }
*/

    /**
     * Method description
     *
     * @return Document me!
     */
/*
    public Icon getIcon() {
        return GuiUtils
            .createImageIcon(PluginConstants
                .ICON_APPLICATION_BIG);    // To change body of implemented methods use File | Settings | File Templates.
    }
*/
    private void readPomFiles(VirtualFile virtualFile, Set<MavenProjectDocument> pomFileList) {
        VirtualFile[] children = virtualFile.getChildren();

        if (children == null) {
            return;
        }

        for (VirtualFile child : children) {
            if (child.getName().equals(PluginConstants.POM_FILE_NAME)) {
                if (!isPomInList(pomFileList, child)) {
                    pomFileList.add(new MavenProjectDocumentImpl(child));
                }
            }

            readPomFiles(child, pomFileList);
        }
    }

    private Set<MavenProjectDocument> getPomFilesOfProject(ProjectRootManager projectRootManager) {
        VirtualFile[] contentRoots = projectRootManager.getContentRoots();

        for (VirtualFile contentRoot : contentRoots) {
            readPomFiles(contentRoot, mavenPomList);
        }

        return mavenPomList;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public boolean isModified() {
        MavenProjectConfigurationForm form =
                (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
        MavenBuildPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

        return (form != null) && form.isModified(pluginSettings);
    }

    private boolean isPomInList(Set<MavenProjectDocument> pomFileList, VirtualFile child) {
        for (MavenProjectDocument existingEntry : pomFileList) {
            if (existingEntry.getPomFile().getPath().equals(child.getPath())) {
                return true;
            }
        }

        return false;
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
