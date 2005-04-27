package org.mevenide.idea.util.ui.tree;

import org.mevenide.idea.GoalsProvider;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Arik
 */
public class SimpleGoalsTreeModel extends AbstractTreeModel {

    public SimpleGoalsTreeModel(final GoalsProvider pProvider) {
        super(new DefaultMutableTreeNode());

        final MutableTreeNode root = getMutableRoot();
        final String[] plugins = pProvider.getPlugins();
        for (int i = 0; i < plugins.length; i++) {

            final String plugin = plugins[i];
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = pProvider.getGoals(plugin);
            for (int j = 0; j < goals.length; j++) {

                final String goal = goals[j];
                final MutableTreeNode goalNode = new GoalTreeNode(
                                goal,
                                pProvider.getDescription(goal),
                                pProvider.getPrereqs(goal));
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            root.insert(pluginNode, root.getChildCount());
        }
    }
}
