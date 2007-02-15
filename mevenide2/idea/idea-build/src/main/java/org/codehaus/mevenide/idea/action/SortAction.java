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
import com.intellij.openapi.actionSystem.Presentation;

import org.apache.log4j.Logger;

import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.util.Collections;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class SortAction extends AbstractBaseAction {
    private static final Logger LOG = Logger.getLogger(SortAction.class);

    /**
     * Constructs ...
     */
    public SortAction() {}

    /**
     * Constructs ...
     *
     * @param context     Document me!
     * @param text        Document me!
     * @param description Document me!
     * @param icon        Document me!
     */
    public SortAction(ActionContext context, String text, String description, Icon icon) {
        super(text, description, icon);
        this.actionContext = context;
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(AnActionEvent actionEvent) {
        String actionText = actionEvent.getPresentation().getText();

        if (actionText.equals(PluginConstants.ACTION_COMMAND_SORT_ASC)) {
            sortNodesAscending(actionContext);
        }
    }

    /**
     * Method description
     *
     * @param e Document me!
     */
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        if ((actionContext != null) && (actionContext.getGuiContext().getMavenToolWindowForm() != null)) {
            DefaultMutableTreeNode selectedNode =
                GuiUtils
                    .getSelectedNodeObject(((MavenBuildProjectToolWindowForm) actionContext.getGuiContext()
                        .getMavenToolWindowForm()).getPomTree());

            if (selectedNode == null) {
                presentation.setEnabled(false);

                return;
            }

            if (!selectedNode.isLeaf()) {
                presentation.setEnabled(true);
            } else {
                presentation.setEnabled(false);
            }
        }
    }

    /**
     * Method description
     *
     * @param context Document me!
     */
    private void sortNodesAscending(ActionContext context) {
        LOG.debug("Sorting nodes in ascending order");

        PomTree pomTree =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
        TreePath[] selectedPaths = pomTree.getSelectionPaths();
        DefaultMutableTreeNode rootNode = sortTree(GuiUtils.getSelectedNodeObject(pomTree));
        Enumeration expandedPaths = pomTree.getExpandedDescendants(
                                        new TreePath(
                                            ((DefaultMutableTreeNode) pomTree.getModel().getRoot()).getPath()));
        DefaultTreeModel model = (DefaultTreeModel) pomTree.getModel();

        if (GuiUtils.getSelectedNodeObject(pomTree).isRoot()) {
            model.setRoot(rootNode);
            Collections.sort(context.getPomDocumentList());
        }

        model.reload();

        if (expandedPaths != null) {
            while (expandedPaths.hasMoreElements()) {
                pomTree.expandPath((TreePath) expandedPaths.nextElement());
            }
        }

        if (selectedPaths != null) {
            pomTree.setSelectionPaths(selectedPaths);
        }
    }

    /**
     * @param startNode of tree
     *
     * @return sorted elements alphabetically
     */
    private DefaultMutableTreeNode sortTree(DefaultMutableTreeNode startNode) {
        boolean sorted;

        if (startNode != null) {
            do {
                sorted = true;

                for (int i = 1; i < startNode.getChildCount(); i++) {
                    DefaultMutableTreeNode nodeLeft = (DefaultMutableTreeNode) startNode.getChildAt(i - 1);
                    DefaultMutableTreeNode nodeRight = (DefaultMutableTreeNode) startNode.getChildAt(i);

                    if (nodeLeft.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                        continue;
                    }

                    if (nodeLeft.getUserObject().toString().compareToIgnoreCase(nodeRight.getUserObject().toString())
                            > 0) {
                        startNode.insert(nodeRight, i - 1);
                        startNode.insert(nodeLeft, i);
                        sorted = false;
                    }
                }
            } while (!sorted);
        }

        return startNode;
    }
}
