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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.build.IMavenBuildLogger;
import org.codehaus.mevenide.idea.build.MavenBuildLogger;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.PomTreeUtil;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectOutputForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenPluginDocumentImpl;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * @param context Document me!
     *
     * @throws IOException
     * @throws IdeaMavenPluginException
     */
    public static void chooseAndAddPomToTree(ActionContext context)
            throws IOException, IdeaMavenPluginException {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
        VirtualFile[] pomFiles = FileChooser.chooseFiles(context.getPluginProject(), descriptor);

        if (pomFiles != null) {
            for (VirtualFile pomFile : pomFiles) {
                MavenProjectDocument mavenProjectDocument = ModelUtils.loadMavenProjectDocument(context, pomFile);
                if (mavenProjectDocument!=null) {
                    PomTreeUtil.addSinglePomToTree(context, mavenProjectDocument);
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
        PomTree tree = PomTreeUtil.getPomTree(context);

        for (DefaultMutableTreeNode node : GuiUtils.getSelectedNodeObjects(tree)) {
            MavenProjectDocument document = PomTreeUtil.getMavenProjectDocument(node);
            if (document != null) {
                removePomFromTree(context, node, tree);
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
            MavenProjectDocument mavenProjectDocument = PomTreeUtil.getMavenProjectDocument(node);
            if (mavenProjectDocument!=null) {
                GuiUtils.removeAndSelectParent(tree, node);

                LOG.debug("Removing POM: " + mavenProjectDocument.getProject().getName());
                context.getPomDocumentList().remove(mavenProjectDocument);
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     * @param pomTree
     */
    public static void chooseAndAddPluginToPom(ActionContext context, PomTree pomTree) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, false, true, true, false, true);
        VirtualFile[] pluginFiles = FileChooser.chooseFiles(context.getPluginProject(), descriptor);

        for (VirtualFile pluginFile : pluginFiles) {
            try {
                MavenPluginDocument mavenPluginDocument = ModelUtils.createMavenPluginDocument(pluginFile.getPath(), true);
                if (mavenPluginDocument!=null) {
                    addPluginToTree(pomTree, mavenPluginDocument);
                }
            } catch (Exception e) {
                ErrorHandler.processAndShowError(context.getPluginProject(), e);
            }
        }
    }

    /**
     * Method description
     *
     * @param pomTree
     * @param mavenPluginDocument
     * @throws IOException
     * @throws IdeaMavenPluginException
     *
     */
    private static void addPluginToTree(PomTree pomTree, MavenPluginDocument mavenPluginDocument) {

        DefaultMutableTreeNode node = GuiUtils.getSelectedNodeObject(pomTree);
        MavenProjectDocument document = PomTreeUtil.getMavenProjectDocument( node);

        if (document != null) {
            document.addPlugin(mavenPluginDocument);
            PomTreeUtil.addPluginToPomTree(pomTree, node, mavenPluginDocument);
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void removeSelectedPluginsFromPom(ActionContext context) {
        PomTree tree = PomTreeUtil.getPomTree(context);
        List<DefaultMutableTreeNode> nodeList = GuiUtils.getSelectedNodeObjects(tree);

        if (GuiUtils.allNodesAreOfTheSameType(nodeList, MavenPluginDocumentImpl.class)) {
            for (DefaultMutableTreeNode node : nodeList) {
                if (!node.isRoot()) {
                    removePluginFromTree(tree, node);
                }
            }
        }
    }

    /**
     * Method description
     *
     * @param tree Document me!
     * @param node Document me!
     */
    private static void removePluginFromTree(PomTree tree, DefaultMutableTreeNode node) {

        MavenPluginDocument pluginDocument = (MavenPluginDocument) node.getUserObject();

        if ((node.getFirstChild() != null)
                && PomTreeUtil.isPluginGoal(((DefaultMutableTreeNode) node.getFirstChild()).getUserObject())) {

            GuiUtils.removeAndSelectParent(tree, node);

            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            MavenProjectDocument mavenProjectDocument = PomTreeUtil.getMavenProjectDocument(parent);
            mavenProjectDocument.removePlugin(pluginDocument);
        }

    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    public static void openPom(ActionContext context) {
        PomTree tree = PomTreeUtil.getPomTree(context);

        for (DefaultMutableTreeNode node : GuiUtils.getSelectedNodeObjects(tree)) {
            MavenProjectDocument document = PomTreeUtil.getMavenProjectDocument( node);
            if (document != null) {
              FileEditorManager.getInstance(context.getPluginProject()).openFile(document.getPomFile(), true);
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
                    PomTreeUtil.getPomTree(context));
        }

        if ((selectedNodeList != null) && (selectedNodeList.size() > 0)
                && PomTreeUtil.nodesAreExecutableMavenGoals(selectedNodeList)) {
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
