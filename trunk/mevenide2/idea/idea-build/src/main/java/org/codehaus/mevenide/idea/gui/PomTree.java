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



package org.codehaus.mevenide.idea.gui;

import com.intellij.util.ui.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PomTree extends Tree {
    private DefaultTreeModel defaultTreeModel;
    private DefaultMutableTreeNode rootNode;

    /**
     * Constructs ...
     *
     * @param rootNodeTitle Document me!
     * @param pomIcon       Document me!
     * @param goalIcon      Document me!
     */
    public PomTree(String rootNodeTitle, Icon pomIcon, Icon goalIcon) {
        super();
        rootNode = new DefaultMutableTreeNode(rootNodeTitle);
        defaultTreeModel = new DefaultTreeModel(rootNode);
        setModel(defaultTreeModel);
        setEditable(true);
        setShowsRootHandles(true);

//      addMouseListener(new PomTreeMouseActionListener(context));
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        setCellRenderer(new PomTreeCellRenderer(pomIcon, goalIcon));

//      addTreeSelectionListener(new PomTreeSelectionListener(this));
    }

    /**
     * Add child to the currently selected node.
     *
     * @param child Document me!
     *
     * @return Document me!
     */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode;
        TreePath parentPath = getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    /**
     * Method description
     *
     * @param parent Document me!
     * @param child  Document me!
     *
     * @return Document me!
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
        return addObject(parent, child, false);
    }

    /**
     * Method description
     *
     * @param parent          Document me!
     * @param child           Document me!
     * @param shouldBeVisible Document me!
     *
     * @return Document me!
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        defaultTreeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        rootNode.removeAllChildren();
        defaultTreeModel.reload();
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = getSelectionPath();

        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());

            if (parent != null) {
                defaultTreeModel.removeNodeFromParent(currentNode);
            }
        }
    }
}
