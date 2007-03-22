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


package org.codehaus.mevenide.idea.util;

import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class GuiUtils {
    private static final Logger LOG = Logger.getLogger(GuiUtils.class);

    /**
     * Builds the menu items.
     *
     * @param menu           The menu to plase the items into.
     * @param menuItemText   The menu text of the newly created menu item.
     * @param actionListener The action listener to register.
     * @return Document me!
     */
    public static JMenuItem addMenuItem(JMenu menu, String menuItemText, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(menuItemText);

        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        return menuItem;
    }

    /**
     * Builds the menu items.
     *
     * @param popupMenu      The menu to plase the items into.
     * @param menuItemText   The menu text of the newly created menu item.
     * @param actionListener The action listener to register.
     * @return Document me!
     */
    public static JMenuItem addMenuItem(JPopupMenu popupMenu, String menuItemText, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(menuItemText);

        menuItem.addActionListener(actionListener);
        popupMenu.add(menuItem);

        return menuItem;
    }

    /**
     * Adds a checkbox menu item to a given popupMenu.
     *
     * @param popupMenu      The menu to plase the items into.
     * @param menuItemText   The menu text of the newly created menu item.
     * @param actionListener The action listener to register.
     * @return Document me!
     */
    public static JCheckBoxMenuItem addCheckBoxMenuItem(JPopupMenu popupMenu, String menuItemText,
                                                        ActionListener actionListener) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(menuItemText);

        menuItem.addActionListener(actionListener);
        menuItem.setActionCommand(menuItemText);
        menuItem.setSelected(false);
        popupMenu.add(menuItem);

        return menuItem;
    }

    /**
     * Builds the menu items.
     *
     * @param menu           The menu to plase the items into.
     * @param menuItemText   The menu text of the newly created menu item.
     * @param actionListener The action listener to register.
     * @param isEnabled      True if the menu item should be enabled, false otherwise.
     * @return Document me!
     */
    public static JMenuItem addMenuItem(JMenu menu, String menuItemText, ActionListener actionListener,
                                        boolean isEnabled) {
        JMenuItem menuItem = new JMenuItem(menuItemText);

        menuItem.addActionListener(actionListener);
        menuItem.setEnabled(isEnabled);
        menu.add(menuItem);

        return menuItem;
    }

    /**
     * Method description
     *
     * @param obj Document me!
     * @return Document me!
     */
    public static DefaultMutableTreeNode createDefaultTreeNode(Object obj) {
        return new DefaultMutableTreeNode(obj);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path Document me!
     * @return the image icon.
     */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GuiUtils.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            LOG.error("Couldn't find file: " + path);

            return null;
        }
    }

    /**
     * Gets the selected node of a tree.
     *
     * @param tree The tree.
     * @return The node or null in case no node is selected.
     */
    public static DefaultMutableTreeNode getSelectedNodeObject(JTree tree) {
        if (tree != null) {
            TreePath selPath = tree.getSelectionPath();

            if (selPath != null) {
                return (DefaultMutableTreeNode) selPath.getLastPathComponent();
            }
        }

        return null;
    }

    /**
     * Gets the selected node of a tree at the specified location.
     *
     * @param tree The tree.
     * @param x    The x coordinate.
     * @param y    The y coordinate
     * @return The node or null in case no node is selected.
     */
    public static DefaultMutableTreeNode getSelectedNodeObject(JTree tree, int x, int y) {
        TreePath selPath = tree.getPathForLocation(x, y);

        if (selPath != null) {
            return (DefaultMutableTreeNode) selPath.getLastPathComponent();
        }

        return null;
    }

    /**
     * Gets the selected nodes of a tree.
     *
     * @param tree The tree.
     * @return The list of nodes or null in case no node is selected.
     */
    public static List<DefaultMutableTreeNode> getSelectedNodeObjects(JTree tree) {
        List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

        if (tree != null) {
            TreePath[] selPaths = tree.getSelectionPaths();

            if (selPaths != null) {
                for (TreePath selPath : selPaths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                    nodeList.add(node);
                }
            }
        }

        return nodeList;
    }

    /**
     * Gets the selected nodes of a tree, sorted by phases and goals.
     *
     * @param tree The tree.
     * @return The list of nodes or null in case no node is selected.
     */
    public static List<DefaultMutableTreeNode> getSortedSelectedNodeObjects(JTree tree) {
        List<DefaultMutableTreeNode> nodeList = getSelectedNodeObjects(tree);
        List<DefaultMutableTreeNode> sortedPhasesList = new ArrayList<DefaultMutableTreeNode>();
        DefaultMutableTreeNode goalsParentNode = null;

        if (!nodeList.isEmpty()) {
            for (DefaultMutableTreeNode aNodeList : nodeList) {
                goalsParentNode = (DefaultMutableTreeNode) aNodeList.getParent();

                if ((goalsParentNode.getUserObject() != null)
                        && goalsParentNode.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                    break;
                }
            }

            if (goalsParentNode != null) {
                List<DefaultMutableTreeNode> parentChildren = new ArrayList<DefaultMutableTreeNode>();

                for (int i = 0; i < goalsParentNode.getChildCount(); i++) {
                    parentChildren.add((DefaultMutableTreeNode) goalsParentNode.getChildAt(i));
                }

                for (DefaultMutableTreeNode child : parentChildren) {
                    if (nodeList.contains(child)) {
                        sortedPhasesList.add(child);
                        nodeList.remove(child);
                    }
                }
            }
        }

        sortedPhasesList.addAll(nodeList);

        return sortedPhasesList;
    }

    /**
     * Method description
     *
     * @param selectedNodeList Document me!
     * @param clazz            Document me!
     * @return Document me!
     */
    public static boolean allNodesAreOfTheSameType(List<DefaultMutableTreeNode> selectedNodeList, Object clazz) {
        for (DefaultMutableTreeNode node : selectedNodeList) {
            Object nodeInfo = node.getUserObject();

            if ((nodeInfo != null) && nodeInfo.getClass().equals(clazz)) {
            } else {
                return false;
            }
        }

        return true;
    }

    public static DefaultMutableTreeNode findNodeByObject(DefaultMutableTreeNode root, Object object) {
        if ( root.getUserObject() == object ) {
            return root;
        }
        for ( int i = 0; i != root.getChildCount(); i++ ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            DefaultMutableTreeNode found = findNodeByObject(child, object);
            if ( found != null ) {
                return found;
            }
        }
        return null;
    }

    public static void removeAndSelectParent(JTree tree, DefaultMutableTreeNode node) {
        DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
        TreeNode[] path = treeModel.getPathToRoot(node.getParent());
        treeModel.removeNodeFromParent(node);
        tree.setSelectionPath(new TreePath(path));
    }
}
