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
package org.mevenide.idea.module.ui;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;
import org.mevenide.idea.util.ui.tree.SimpleGoalsTreeModel;
import org.mevenide.idea.util.ui.tree.checkbox.MutableCheckBoxTreeModel;
import org.mevenide.idea.util.goals.GoalsHelper;

import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * @author Arik
 */
public class GoalsSelectionTreeModel extends SimpleGoalsTreeModel implements MutableCheckBoxTreeModel {

    private final Set<String> selectedGoals = new HashSet<String>(10);

    public GoalsSelectionTreeModel(final IGoalsGrabber pProvider) {
        super(pProvider);
    }

    public void setSelectedGoals(final Collection<String> pGoals) {
        selectedGoals.addAll(pGoals);
    }

    public Collection<String> getSelectedGoals() {
        return Collections.unmodifiableCollection(selectedGoals);
    }

    public boolean isCheckable(TreeNode pNode) {
        return shouldDisplayCheckBox(pNode);
    }

    public boolean isChecked(TreeNode pNode) {
        if(pNode instanceof GoalTreeNode) {
            final GoalTreeNode node = (GoalTreeNode) pNode;
            final PluginTreeNode parent = (PluginTreeNode) node.getParent();
            final String plugin = parent.getPlugin();
            final String goal = node.getGoal();
            final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
            return selectedGoals.contains(fqName);
        }
        else if(pNode instanceof PluginTreeNode) {
            final PluginTreeNode node = (PluginTreeNode) pNode;
            if(node.getChildCount() == 0)
                return false;

            final Enumeration children = node.children();
            while (children.hasMoreElements()) {
                final TreeNode child = (TreeNode) children.nextElement();
                final boolean checked = shouldDisplayCheckBox(child) && isChecked(child);
                if(!checked)
                    return false;
            }
            return true;
        }
        else
            return false;
    }

    public void setChecked(final TreeNode pNode, final boolean pChecked) {
        if (pNode instanceof GoalTreeNode) {
            final GoalTreeNode node = (GoalTreeNode) pNode;
            setGoalChecked(node, pChecked);

            //since checking a goal might change how the plugin is
            //displayed, notify about the parent as well
            nodeChanged(node.getParent());
        }
        else if (pNode instanceof PluginTreeNode) {
            final PluginTreeNode node = (PluginTreeNode) pNode;
            final Enumeration children = node.children();
            while (children.hasMoreElements()) {
                final TreeNode child = (TreeNode) children.nextElement();
                if (shouldDisplayCheckBox(child) && isCheckable(child)) {
                    if(child instanceof GoalTreeNode)
                        setGoalChecked((GoalTreeNode) child, pChecked);
                    else
                        setChecked(child, pChecked);
                }
            }

            //
            //notify listeners that this node has changed, if
            //requested to
            //
            nodeChanged(node);
        }
    }

    private void setGoalChecked(final GoalTreeNode pNode,
                                final boolean pChecked) {
        final PluginTreeNode parent = (PluginTreeNode) pNode.getParent();
        final String plugin = parent.getPlugin();
        final String goal = pNode.getGoal();
        final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
        if (pChecked)
            selectedGoals.add(fqName);
        else
            selectedGoals.remove(fqName);

        nodeChanged(pNode);
    }

    public boolean shouldDisplayCheckBox(TreeNode pNode) {
        return pNode instanceof GoalTreeNode ||
                pNode instanceof PluginTreeNode;
    }
}
