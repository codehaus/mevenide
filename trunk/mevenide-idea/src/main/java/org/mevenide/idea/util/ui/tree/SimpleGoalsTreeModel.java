/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.util.ui.tree;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.goals.GoalsHelper;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author Arik
 */
public class SimpleGoalsTreeModel extends AbstractTreeModel
{
    public SimpleGoalsTreeModel(final IGoalsGrabber pProvider) {
        super(new DefaultMutableTreeNode());

        if(pProvider == null)
            return;

        //we need a mutable node because we are adding nodes to it
        final MutableTreeNode root = getMutableRoot();

        //iterate over available plugins
        final String[] plugins = pProvider.getPlugins();
        for (final String plugin : plugins) {
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = pProvider.getGoals(plugin);
            for (final String goal : goals) {
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final MutableTreeNode goalNode = new GoalTreeNode(
                        goal,
                        pProvider.getDescription(fqName),
                        pProvider.getPrereqs(fqName));
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            root.insert(pluginNode, root.getChildCount());
        }
    }

    public void addGoal(final String pPlugin,
                        final String pGoal,
                        final String pDescription,
                        final String[] pPrereqs) {
        MutableTreeNode pluginNode = (MutableTreeNode) findNode(root, new PluginVisitor(pPlugin));
        if(pluginNode == null) {
            pluginNode = new PluginTreeNode(pPlugin);
            final GoalTreeNode goalNode = new GoalTreeNode(pGoal, pDescription, pPrereqs);
            pluginNode.insert(goalNode, 0);
            getMutableRoot().insert(pluginNode, 0);
            nodeStructureChanged(root);
//            nodesWereInserted(root, new int[]{0});
        }
        else {
            GoalTreeNode goalNode = (GoalTreeNode) findNode(pluginNode, new GoalVisitor(pPlugin, pGoal));
            if(goalNode != null)
                return;

            goalNode = new GoalTreeNode(pGoal, pDescription, pPrereqs);
            pluginNode.insert(goalNode, pluginNode.getChildCount());
            nodesWereInserted(pluginNode, new int[]{pluginNode.getIndex(goalNode)});
        }
    }

    private static class PluginVisitor implements NodeVisitor
    {
        private final String plugin;

        public PluginVisitor(final String pPlugin) {
            plugin = pPlugin;
        }

        public boolean accept(TreeNode pNode) {
            if (pNode instanceof PluginTreeNode) {
                final PluginTreeNode node = (PluginTreeNode) pNode;
                return node.getPlugin().equals(plugin);
            }
            else
                return false;
        }
    }

    private static class GoalVisitor extends PluginVisitor
    {
        private final String goal;

        public GoalVisitor(final String pPlugin, final String pGoal) {
            super(pPlugin);
            goal = pGoal;
        }

        public boolean accept(TreeNode pNode) {
            if (pNode instanceof GoalTreeNode) {
                final GoalTreeNode node = (GoalTreeNode) pNode;
                final PluginTreeNode parent = (PluginTreeNode) pNode.getParent();
                return node.getGoal().equals(goal) &&
                        super.accept(parent);
            }
            else
                return false;
        }
    }
}
