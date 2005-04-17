package org.mevenide.idea.settings.module;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.goalstoolwindow.GoalTreeNode;
import org.mevenide.idea.goalstoolwindow.PluginTreeNode;
import org.mevenide.idea.support.ui.AbstractTreeModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Arik
 */
public class MavenGoalsTreeModel extends AbstractTreeModel {

    public MavenGoalsTreeModel(final IGoalsGrabber goalsGrabber) {
        super(new DefaultMutableTreeNode());

        final MutableTreeNode root = getMutableRoot();
        final String[] plugins = goalsGrabber.getPlugins();
        for (int i = 0; i < plugins.length; i++) {

            final String plugin = plugins[i];
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = goalsGrabber.getGoals(plugin);
            for (int j = 0; j < goals.length; j++) {
                final String goal = goals[j];
                final MutableTreeNode goalNode = new GoalTreeNode(goal);
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            root.insert(pluginNode, root.getChildCount());
        }
    }
}
