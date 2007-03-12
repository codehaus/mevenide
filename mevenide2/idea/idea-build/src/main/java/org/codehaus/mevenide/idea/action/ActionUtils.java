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

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoDocument;
import org.apache.maven.plugin.PluginDocument;
import org.apache.maven.pom.x400.Plugin;
import org.apache.maven.pom.x400.ProjectDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import org.codehaus.mevenide.idea.build.IMavenBuildLogger;
import org.codehaus.mevenide.idea.build.MavenBuildLogger;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.config.GoalDocument;
import org.codehaus.mevenide.idea.config.NameDocument;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectOutputForm;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenPluginDocumentImpl;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocumentImpl;
import org.codehaus.mevenide.idea.model.PluginGoal;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.*;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class ActionUtils {
    private static final Logger LOG = Logger.getLogger(ActionUtils.class);

    /**
     * Method description
     *
     * @param context              Document me!
     * @param mavenProjectDocument Document me!
     *
     * @return Document me!
     *
     * @throws IOException
     * @throws org.codehaus.mevenide.idea.util.IdeaMavenPluginException
     *
     * @throws XmlException
     */
    public static DefaultMutableTreeNode addSinglePomToTree(ActionContext context,
            MavenProjectDocument mavenProjectDocument)
            throws IOException, XmlException, IdeaMavenPluginException {
        if (mavenProjectDocument.getPomFile().getName().equals(PluginConstants.MAVEN_POM_FILENAME)) {
            DefaultMutableTreeNode rootNode =
                (DefaultMutableTreeNode) ((MavenBuildProjectToolWindowForm) context.getGuiContext()
                    .getMavenToolWindowForm()).getPomTree().getModel().getRoot();
            ProjectDocument projectDocument;

            try {
                XmlOptions xmlOptions = new XmlOptions();
                Map<String, String> xmlOptionsMap = new Hashtable<String, String>();

                xmlOptionsMap.put("", "http://maven.apache.org/POM/4.0.0");
                xmlOptions.setLoadSubstituteNamespaces(xmlOptionsMap);
                projectDocument = ProjectDocument.Factory.parse(new File(mavenProjectDocument.getPomFile().getPath()),
                        xmlOptions);
            } catch (Exception e) {
                LOG.error(e);

                return null;
            }

            // Register a document listener that listens for changes
            FileDocumentManager documentManager = FileDocumentManager.getInstance();
            Document document = documentManager.getDocument(mavenProjectDocument.getPomFile());

            document.addDocumentListener(new PomDocumentListener(mavenProjectDocument));
            mavenProjectDocument.setProjectDocument(projectDocument);
            context.getPomDocumentList().add(mavenProjectDocument);

            DefaultMutableTreeNode childNode =
                ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree()
                    .addObject(rootNode, mavenProjectDocument);

            addStandardPhasesToPomTree(context, childNode);
            addMavenPluginDocumentToMavenProjectDocument(mavenProjectDocument, context);

            for (MavenPluginDocument mavenPluginDocument : mavenProjectDocument.getPluginDocumentList()) {
                addPluginToPomTree(context, childNode, mavenPluginDocument);
            }

            return childNode;
        }

        return null;
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void chooseAndAddPluginToPom(ActionContext context) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, false, true, true, false, true);
        VirtualFile[] pluginFiles = FileChooser.chooseFiles(context.getPluginProject(), descriptor);

        for (VirtualFile pluginFile : pluginFiles) {
            try {
                createPluginAndAddToTree(context, pluginFile);
            } catch (Exception e) {
                ErrorHandler.processAndShowError(context.getPluginProject(), e);
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     *
     * @throws IOException
     * @throws IdeaMavenPluginException
     * @throws XmlException
     */
    public static void chooseAndAddPomToTree(ActionContext context)
            throws IOException, XmlException, IdeaMavenPluginException {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
        VirtualFile[] pomFiles = FileChooser.chooseFiles(context.getPluginProject(), descriptor);

        if (pomFiles != null) {
            for (VirtualFile pomFile : pomFiles) {
                addSinglePomToTree(context, new MavenProjectDocumentImpl(pomFile));
            }
        }
    }

    /**
     * Method description
     *
     * @param pluginFile      Document me!
     * @param isManuallyAdded Document me!
     *
     * @return Document me!
     *
     * @throws IOException
     * @throws IdeaMavenPluginException
     * @throws XmlException
     */
    public static MavenPluginDocument createMavenPluginDocument(VirtualFile pluginFile, boolean isManuallyAdded)
            throws IOException, XmlException, IdeaMavenPluginException {
        if (pluginFile != null) {
            ZipFile jarArchive = new ZipFile(pluginFile.getPath());
            ZipEntry entry = jarArchive.getEntry(PluginConstants.MAVEN_PLUGIN_DESCRIPTOR);

            if (entry != null) {
                Set<PluginGoal> pluginGoalList = new LinkedHashSet<PluginGoal>();
                XmlOptions xmlOptions = new XmlOptions();
                Map<String, String> xmlOptionsMap = new Hashtable<String, String>();

                xmlOptionsMap.put("", "org/apache/maven/plugin");
                xmlOptions.setLoadSubstituteNamespaces(xmlOptionsMap);

                PluginDocument pluginDocument = PluginDocument.Factory.parse(jarArchive.getInputStream(entry),
                                                    xmlOptions);
                MavenPluginDocument mavenPluginDocument = new MavenPluginDocumentImpl(pluginDocument);
                List<MojoDocument.Mojo> mojos = pluginDocument.getPlugin().getMojos().getMojoList();

                for (MojoDocument.Mojo mojo : mojos) {
                    PluginGoal pluginGoal = new PluginGoal();

                    pluginGoal.setPluginPrefix(pluginDocument.getPlugin().getGoalPrefix());
                    pluginGoal.setGoal(mojo.getGoal());
                    pluginGoalList.add(pluginGoal);
                }

                mavenPluginDocument.setPluginGoalList(pluginGoalList);
                mavenPluginDocument.setPluginPath(pluginFile.getPath());
                mavenPluginDocument.setMemberOfPom(!isManuallyAdded);

                return mavenPluginDocument;
            } else {
                throw new IdeaMavenPluginException("Selected archive is not a Maven 2 plugin!");
            }
        }

        return null;
    }

    /**
     * Method description
     *
     * @param selectedNodeList Document me!
     *
     * @return Document me!
     */
    public static boolean nodesAreExecutableMavenGoals(List<DefaultMutableTreeNode> selectedNodeList) {
        for (DefaultMutableTreeNode node : selectedNodeList) {
            Object nodeInfo = node.getUserObject();

            if ((nodeInfo != null)
                    && ((nodeInfo instanceof NameDocument.Name.Enum) || (nodeInfo instanceof PluginGoal))) {}
            else {
                return false;
            }
        }

        return true;
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void openPom(ActionContext context) {
        PomTree tree =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
        List<DefaultMutableTreeNode> nodeList = GuiUtils.getSelectedNodeObjects(tree);

        for (DefaultMutableTreeNode node : nodeList) {
            Object nodeInfo = node.getUserObject();
            FileEditorManager manager = FileEditorManager.getInstance(context.getPluginProject());

            if (!node.isRoot()) {
                if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                    MavenProjectDocument document = (MavenProjectDocument) nodeInfo;

                    manager.openFile(document.getPomFile(), true);
                }
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void removePomFromTree(ActionContext context) {
        PomTree tree =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
        List<DefaultMutableTreeNode> nodeList = GuiUtils.getSelectedNodeObjects(tree);

        for (DefaultMutableTreeNode node : nodeList) {
            Object nodeInfo = node.getUserObject();

            if (!node.isRoot()) {
                if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                    removePomFromTree(context, node, tree);
                }
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void removeSelectedPluginsFromPom(ActionContext context) {
        PomTree tree =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
        List<DefaultMutableTreeNode> nodeList = GuiUtils.getSelectedNodeObjects(tree);

        for (DefaultMutableTreeNode node : nodeList) {
            if (!node.isRoot() && GuiUtils.allNodesAreOfTheSameType(nodeList, MavenPluginDocumentImpl.class)) {
                removePluginFromTree(node, tree);
            }
        }
    }

    /**
     * Method description
     *
     * @param context          Document me!
     * @param selectedNodeList Document me!
     *
     * @throws org.codehaus.mevenide.idea.util.IdeaMavenPluginException
     *          in case of an execution error.
     */
    public static void runSelectedGoals(ActionContext context, List<DefaultMutableTreeNode> selectedNodeList)
            throws IdeaMavenPluginException {
        BuildContext buildContext = new BuildContext();

        buildContext.setActionContext(context);

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document[] documents = fileDocumentManager.getUnsavedDocuments();

        if ((documents != null) && (documents.length > 0)) {
            LOG.debug("Saving all unsaved documents!");
            fileDocumentManager.saveAllDocuments();
        }

        if (selectedNodeList == null) {
            selectedNodeList = GuiUtils.getSortedSelectedNodeObjects(
                ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree());
        }

        if ((selectedNodeList != null) && (selectedNodeList.size() > 0)
                && nodesAreExecutableMavenGoals(selectedNodeList)) {
            DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode) selectedNodeList.get(0).getParent().getParent();
            MavenProjectDocument mavenProject = (MavenProjectDocument) parentNode.getUserObject();
            List<String> goalList = new ArrayList<String>();

            for (DefaultMutableTreeNode node : selectedNodeList) {
                String goalName = node.getUserObject().toString();

                LOG.info("Goal to execute: " + goalName);
                goalList.add(goalName);
            }

            VirtualFile pomFile = mavenProject.getPomFile();

            buildContext.setPomFile(pomFile.getPath());

            if (pomFile.getParent() != null) {
                buildContext.setWorkingDir(pomFile.getParent().getPath());
            }

            buildContext.setGoals(goalList);

            MavenRunner runner = new MavenRunner(buildContext);

            runner.execute();
        }
    }

    private static void addMavenPluginDocumentToMavenProjectDocument(MavenProjectDocument mavenProjectDocument,
            ActionContext context)
            throws IOException, XmlException, IdeaMavenPluginException {
        List<Plugin> pomPluginList = null;

        try {
            pomPluginList =
                mavenProjectDocument.getProjectDocument().getProject().getBuild().getPlugins().getPluginList();
        } catch (NullPointerException e) {
            LOG.warn("Project does not contain any customized plugins");

            return;
        }

        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        for (Plugin pomPlugin : pomPluginList) {
            String groupId = pomPlugin.getGroupId();

            if (StringUtils.isEmpty(groupId)) {
                groupId = "org.apache.maven.plugins";
            }

            String artifactId = pomPlugin.getArtifactId();
            String version = pomPlugin.getVersion();
            String mostRecentVersion;
            String mavenRepository = context.getProjectPluginSettings().getMavenRepository();

            groupId = StringUtils.replace(groupId, ".", System.getProperty("file.separator"));

            String pluginDirectory = mavenRepository + System.getProperty("file.separator") + groupId
                                     + System.getProperty("file.separator") + artifactId;
            VirtualFile pluginDirectoryAsFile = localFileSystem.findFileByIoFile(new File(pluginDirectory));

            if (pluginDirectoryAsFile == null) {
                groupId = "org.codehaus.mojo";
                pluginDirectory = mavenRepository + System.getProperty("file.separator") + groupId
                                  + System.getProperty("file.separator") + artifactId;
                pluginDirectoryAsFile = localFileSystem.findFileByIoFile(new File(pluginDirectory));
            }

            if ((pluginDirectoryAsFile != null) && pluginDirectoryAsFile.isDirectory()) {
                VirtualFile[] availableVersions = pluginDirectoryAsFile.getChildren();
                List<String> directoryList = new ArrayList<String>();

                for (VirtualFile availableVersion : availableVersions) {
                    if (availableVersion.isDirectory()) {
                        directoryList.add(availableVersion.getName());
                    }
                }

                Collections.sort(directoryList);

                if (StringUtils.isEmpty(version) &&!directoryList.isEmpty()) {
                    mostRecentVersion = directoryList.get(directoryList.size() - 1);
                } else {
                    mostRecentVersion = version;
                }

                pluginDirectory = pluginDirectory + System.getProperty("file.separator") + mostRecentVersion;

                File pluginJarArchive = new File(pluginDirectory + System.getProperty("file.separator") + artifactId
                                                 + "-" + mostRecentVersion + ".jar");

                LOG.debug("Adding plugin: " + pluginJarArchive.getAbsolutePath() + " to POM");

                MavenPluginDocument pluginDocument =
                    createMavenPluginDocument(localFileSystem.findFileByIoFile(pluginJarArchive), false);

                if (pluginDocument != null) {
                    mavenProjectDocument.getPluginDocumentList().add(pluginDocument);
                }
            }
        }
    }

    /**
     * Method description
     *
     * @param context             Document me!
     * @param treeRootNode        Document me!
     * @param mavenPluginDocument Document me!
     */
    private static void addPluginToPomTree(ActionContext context, DefaultMutableTreeNode treeRootNode,
            MavenPluginDocument mavenPluginDocument) {
        Set<PluginGoal> pluginGoalList = mavenPluginDocument.getPluginGoalList();

        if (mavenPluginDocument.getPluginGoalList().size() > 0) {
            DefaultMutableTreeNode node =
                ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree()
                    .addObject(treeRootNode, mavenPluginDocument);

            for (PluginGoal goal : pluginGoalList) {
                node.add(GuiUtils.createDefaultTreeNode(goal));
            }
        }
    }

    /**
     * Method description
     *
     * @param context      Document me!
     * @param treeRootNode Document me!
     */
    private static void addStandardPhasesToPomTree(ActionContext context, DefaultMutableTreeNode treeRootNode) {
        List<GoalDocument.Goal> standardGoals =
            context.getProjectPluginConfiguration().getMaven().getGoals().getStandard().getGoalList();
        DefaultMutableTreeNode node =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree().addObject(
                treeRootNode, PluginConstants.NODE_POMTREE_PHASES);

        for (GoalDocument.Goal goal : standardGoals) {
            node.add(GuiUtils.createDefaultTreeNode(goal.getName()));
        }

        if (context.getProjectPluginSettings().isUseFilter()) {
            filterStandardPhasesInNodes(context, node);
        }
    }

    /**
     * Filters standard child nodes of the Phases node. After applying this toggleFilter, only the
     * standard phases as listed below are child nodes of the Phases node.
     * <p/>
     * <ul> <li>clean</li> <li>compile</li> <li>test</li> <li>package</li> <li>install</li> </ul>
     *
     * @param startNode     of tree.
     * @param actionContext The action context.
     *
     * @return the start node.
     */
    public static DefaultMutableTreeNode filterStandardPhasesInNodes(ActionContext actionContext,
            DefaultMutableTreeNode startNode) {
        if (startNode != null) {
            List<String> standardPhasesList = actionContext.getProjectPluginSettings().getStandardPhasesList();

            // traverse the whole tree in case the start node is not a Phases node.
            if (!startNode.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                Enumeration enumeration = startNode.postorderEnumeration();

                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

                    if (node.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                        for (int j = 0; j < node.getChildCount(); j++) {
                            DefaultMutableTreeNode phaseNode = (DefaultMutableTreeNode) node.getChildAt(j);
                            String phaseName = phaseNode.getUserObject().toString();

                            if (!standardPhasesList.contains(phaseName)) {
                                node.remove(phaseNode);
                                j--;
                            }
                        }
                    }
                }

                // only traverse the child nodes below the given Phases start node.
            } else {
                for (int j = 0; j < startNode.getChildCount(); j++) {
                    DefaultMutableTreeNode phaseNode = (DefaultMutableTreeNode) startNode.getChildAt(j);
                    String phaseName = phaseNode.getUserObject().toString();

                    if (!standardPhasesList.contains(phaseName)) {
                        startNode.remove(phaseNode);
                        j--;
                    }
                }
            }
        }

        return startNode;
    }

    /**
     * Unfilters standard child nodes of the Phases node. After applying this toggleFilter, all maven
     * phases are listed as child nodes of the phases node.
     *
     * @param actionContext The action context.
     * @param startNode     of tree.
     *
     * @return the start node.
     */
    public static DefaultMutableTreeNode unfilterStandardPhasesInNodes(ActionContext actionContext,
            DefaultMutableTreeNode startNode) {
        if (startNode != null) {

            // traverse the whole tree in case the start node is not a Phases node.
            if (!startNode.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                Enumeration enumeration = startNode.postorderEnumeration();

                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

                    if (node.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                        node.removeAllChildren();

                        List<GoalDocument.Goal> standardGoals =
                            actionContext.getProjectPluginConfiguration().getMaven().getGoals().getStandard()
                                .getGoalList();

                        for (GoalDocument.Goal goal : standardGoals) {
                            node.add(GuiUtils.createDefaultTreeNode(goal.getName()));
                        }
                    }
                }

                // only traverse the child nodes below the given Phases start node.
            } else {
                startNode.removeAllChildren();

                List<GoalDocument.Goal> standardGoals =
                    actionContext.getProjectPluginConfiguration().getMaven().getGoals().getStandard().getGoalList();

                for (GoalDocument.Goal goal : standardGoals) {
                    startNode.add(GuiUtils.createDefaultTreeNode(goal.getName()));
                }
            }
        }

        return startNode;
    }

    /**
     * Method description
     *
     * @param context    Document me!
     * @param pluginFile Document me!
     *
     * @throws IOException
     * @throws IdeaMavenPluginException
     * @throws org.apache.xmlbeans.XmlException
     *
     */
    private static void createPluginAndAddToTree(ActionContext context, VirtualFile pluginFile)
            throws IOException, org.apache.xmlbeans.XmlException, IdeaMavenPluginException {
        MavenPluginDocument mavenPluginDocument = createMavenPluginDocument(pluginFile, true);

        if (mavenPluginDocument != null) {
            DefaultMutableTreeNode node =
                GuiUtils.getSelectedNodeObject(
                    ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree());
            Object nodeInfo = node.getUserObject();

            if (!node.isRoot()) {
                if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                    ((MavenProjectDocument) nodeInfo).getPluginDocumentList().add(mavenPluginDocument);
                    addPluginToPomTree(context, node, mavenPluginDocument);
                }
            }
        }
    }

    /**
     * Method description
     *
     * @param node Document me!
     * @param tree Document me!
     */
    private static void removePluginFromTree(DefaultMutableTreeNode node, PomTree tree) {
        if (node != null) {
            MavenPluginDocument nodeInfo = (MavenPluginDocument) node.getUserObject();

            if (!node.isRoot() && (node.getFirstChild() != null)
                    && ((DefaultMutableTreeNode) node.getFirstChild()).getUserObject() instanceof PluginGoal) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
                TreeNode[] path = ((DefaultTreeModel) tree.getModel()).getPathToRoot(parent);

                treeModel.removeNodeFromParent(node);
                tree.setSelectionPath(new TreePath(path));

                MavenProjectDocument mavenProjectDocument = (MavenProjectDocument) parent.getUserObject();

                mavenProjectDocument.getPluginDocumentList().remove(nodeInfo);
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     * @param node    Document me!
     * @param tree    Document me!
     */
    private static void removePomFromTree(ActionContext context, DefaultMutableTreeNode node, PomTree tree) {
        if (node != null) {
            Object nodeInfo = node.getUserObject();

            if (!node.isRoot()) {
                if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                    TreeNode parent = node.getParent();
                    DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
                    TreeNode[] path = ((DefaultTreeModel) tree.getModel()).getPathToRoot(parent);

                    treeModel.removeNodeFromParent(node);
                    tree.setSelectionPath(new TreePath(path));

                    MavenProjectDocument mavenProjectDocument = (MavenProjectDocument) nodeInfo;

                    LOG.debug("Removing POM: " + mavenProjectDocument.getProjectDocument().getProject().getName());
                    context.getPomDocumentList().remove(mavenProjectDocument);
                }
            }
        }
    }

    public static void createAndShowOutputConsole(BuildContext buildContext) {
        boolean useEmbedder = buildContext.isUseMavenEmbedder();
        ConsoleView view = buildContext.getConsoleView();
        Project project = buildContext.getActionContext().getPluginProject();
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        final JComponent panel = createOutputPanel(buildContext, useEmbedder, view);
        ToolWindow outputToolWindow = manager.getToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);

        if (outputToolWindow == null) {
            outputToolWindow = manager.registerToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID, panel,
                    ToolWindowAnchor.BOTTOM);
            outputToolWindow.show(null);
        }
    }

    private static JComponent createOutputPanel(BuildContext context, boolean useEmbedder, ConsoleView view) {

        // remove the old listener
        MavenBuildProjectOutputForm oldForm =
            (MavenBuildProjectOutputForm) context.getActionContext().getGuiContext().getMavenOutputWindowForm();
        MavenBuildProjectOutputForm form = createOutputForm(context, view);

        context.getActionContext().getGuiContext().setMavenOutputWindowForm(form);

        if (useEmbedder) {
            MavenBuildLogger mavenLogger = (MavenBuildLogger) context.getLogger();

            mavenLogger.removeListener(oldForm);
            mavenLogger.addListener(form);
        } else {
            IMavenBuildLogger mavenLogger = context.getLogger();

            mavenLogger.removeListener(oldForm);
            mavenLogger.addListener(form);
        }

        return form.getRootComponent();
    }

    private static MavenBuildProjectOutputForm createOutputForm(BuildContext context, ConsoleView view) {
        MavenBuildProjectOutputForm form = new MavenBuildProjectOutputForm(view);
        DefaultActionGroup group = new DefaultActionGroup();
        ActionToolbar actionToolbar;
        AnAction actionRerun = new RerunMavenAction(context, PluginConstants.ACTION_COMMAND_RERUN_MAVEN,
                                   PluginConstants.ACTION_COMMAND_RERUN_MAVEN,
                                   IconLoader.getIcon(PluginConstants.ICON_RERUN));
        AnAction actionPauseOutput = new PauseOutputAction(context, PluginConstants.ACTION_COMMAND_PAUSE_OUTPUT,
                                         PluginConstants.ACTION_COMMAND_PAUSE_OUTPUT,
                                         IconLoader.getIcon(PluginConstants.ICON_PAUSE));
        AnAction actionStopBuild = new StopProcessAction(context, PluginConstants.ACTION_COMMAND_STOP_PROCESS,
                                       PluginConstants.ACTION_COMMAND_STOP_PROCESS,
                                       IconLoader.getIcon(PluginConstants.ICON_STOP));
        AnAction closeOutputPanel = new CloseOutputPanelAction(context,
                                        PluginConstants.ACTION_COMMAND_CLOSE_OUTPUT_PANEL,
                                        PluginConstants.ACTION_COMMAND_CLOSE_OUTPUT_PANEL,
                                        IconLoader.getIcon(PluginConstants.ICON_CLOSE));

        group.add(actionRerun);
        group.add(actionPauseOutput);
        group.add(actionStopBuild);
        group.add(closeOutputPanel);
        actionToolbar = ActionManager.getInstance().createActionToolbar("Maven Build Output Toolbar", group, false);
        form.getRootComponent().add(actionToolbar.getComponent(),
                                    new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                                        GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED,
                                        GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));

        return form;
    }
}
