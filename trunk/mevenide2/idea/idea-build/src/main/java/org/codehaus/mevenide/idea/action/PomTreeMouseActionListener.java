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

import org.apache.log4j.Logger;

import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.config.NameDocument;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocumentImpl;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocumentImpl;
import org.codehaus.mevenide.idea.model.PluginGoal;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PomTreeMouseActionListener extends AbstractTreeMouseActionListener {
    private static final Logger LOG = Logger.getLogger(PomTreeMouseActionListener.class);
    private PomTree tree;

    /**
     * Constructs ...
     */
    public PomTreeMouseActionListener() {}

    /**
     * Constructs ...
     *
     * @param context Document me!
     */
    public PomTreeMouseActionListener(ActionContext context) {
        this.context = context;
        this.tree = ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
    }

    private JPopupMenu createPopup(List<DefaultMutableTreeNode> nodeList) {
        JPopupMenu popupMenu = new JPopupMenu();

        if ((nodeList.size() == 1) && nodeList.get(0).isRoot()) {
            GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_ADD_POM,
                                 new PomTreeMenuActionListener(context));
        } else if (GuiUtils.allNodesAreOfTheSameType(nodeList, MavenProjectDocumentImpl.class)) {
            GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_OPEN_POM,
                                 new PomTreeMenuActionListener(context));
            GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_REMOVE_POM,
                                 new PomTreeMenuActionListener(context));
            GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_ADD_PLUGIN,
                                 new PomTreeMenuActionListener(context));
        } else if (!nodeList.get(0).isLeaf() && (nodeList.get(0).getFirstChild() != null)) {
            if (GuiUtils.allNodesAreOfTheSameType(nodeList, MavenPluginDocumentImpl.class)) {
                GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN,
                                     new PomTreeMenuActionListener(context));
            }
        } else if (nodeList.get(0).isLeaf() && ActionUtils.nodesAreExecutableMavenGoals(nodeList)) {
            GuiUtils.addMenuItem(popupMenu, PluginConstants.ACTION_COMMAND_RUN_GOALS,
                                 new PomTreeMenuActionListener(context));
        }

        return popupMenu;
    }

    /**
     * Method description
     *
     * @param e Document me!
     */
    public void mousePressed(MouseEvent e) {
        try {
            if (e.getSource() instanceof PomTree) {
                tree = (PomTree) e.getSource();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    processLeftMouseButtonClick(e);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    processRightMouseButtonClick(e);
                }
            }
        } catch (Exception e1) {
            ErrorHandler.processAndShowError(context.getPluginProject(), e1);
        }
    }

    /**
     * Todo: Reload new POM files, in case they have changed!
     *
     * @param e
     */
    protected void processLeftMouseButtonClick(MouseEvent e) {
        List<DefaultMutableTreeNode> selectedNodeList = GuiUtils.getSelectedNodeObjects(tree);

        if ((selectedNodeList == null) || selectedNodeList.isEmpty()) {
            return;
        }

        if (selectedNodeList.get(0).isLeaf()) {
            if ((selectedNodeList.size() == 1) && (e.getClickCount() == 1)) {
                Object nodeInfo = selectedNodeList.get(0).getUserObject();

                if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                    MavenProjectDocument mavenProjectDocument = (MavenProjectDocument) nodeInfo;

                    LOG.info("Project Name: " + mavenProjectDocument.getProjectDocument().getProject().getName());
                } else if ((nodeInfo != null) && (nodeInfo instanceof NameDocument.Name.Enum)) {
                    NameDocument.Name.Enum goalName = (NameDocument.Name.Enum) nodeInfo;

                    LOG.info("Goal name is: " + goalName);
                }
            } else if (e.getClickCount() == 2) {
                Object nodeInfo = selectedNodeList.get(0).getUserObject();

                if (nodeInfo != null) {
                    if ((nodeInfo instanceof NameDocument.Name.Enum) || (nodeInfo instanceof PluginGoal)) {
                        try {
                            ActionUtils.runSelectedGoals(context, selectedNodeList);
                        } catch (IdeaMavenPluginException e1) {
                            ErrorHandler.processAndShowError(context.getPluginProject(), e1, false);
                        }
                    }
                }
            }
        } else {
            if ((selectedNodeList.size() == 1) && (e.getClickCount() == 2)) {
                Object nodeInfo = selectedNodeList.get(0).getUserObject();

                if (nodeInfo instanceof MavenProjectDocument) {
                    ActionUtils.openPom(context);
                }
            }

            LOG.info("Node selection: " + selectedNodeList.toString());
        }
    }

    /**
     * Method description
     *
     * @param e Document me!
     */
    protected void processRightMouseButtonClick(MouseEvent e) {
        List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

        if ((e.getClickCount() == 1) &&!e.isControlDown()) {
            TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());

            if (selectedPath != null) {
                tree.setSelectionPath(selectedPath);
                nodeList.add((DefaultMutableTreeNode) selectedPath.getLastPathComponent());
            }
        } else if ((e.getClickCount() == 1) && e.isControlDown()) {
            TreePath[] selPaths = tree.getSelectionPaths();
            TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());

            if (selectedPath != null) {
                TreePath[] selectionPaths = new TreePath[selPaths.length + 1];

                System.arraycopy(selPaths, 0, selectionPaths, 0, selPaths.length);
                selectionPaths[selPaths.length] = selectedPath;
                tree.setSelectionPaths(selectionPaths);

                for (TreePath aSelPath : selectionPaths) {
                    nodeList.add((DefaultMutableTreeNode) aSelPath.getLastPathComponent());
                }
            } else {
                for (TreePath aSelPath : selPaths) {
                    nodeList.add((DefaultMutableTreeNode) aSelPath.getLastPathComponent());
                }
            }
        }

        JPopupMenu popupMenu = createPopup(nodeList);

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
}
