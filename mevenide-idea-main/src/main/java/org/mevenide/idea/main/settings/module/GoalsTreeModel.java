package org.mevenide.idea.main.settings.module;

import org.mevenide.idea.main.windows.goals.GoalTreeNode;
import org.mevenide.idea.main.windows.goals.PluginTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.SortedMap;

/**
 * @author Arik
 */
public class GoalsTreeModel extends DefaultTreeModel {

    public GoalsTreeModel(final SortedMap pPlugins) {
        super(new DefaultMutableTreeNode());

        final MutableTreeNode root = (MutableTreeNode) super.root;
        final String[] plugins = (String[]) pPlugins.keySet().toArray(new String[pPlugins.size()]);
        for (int i = 0; i < plugins.length; i++) {

            final String plugin = plugins[i];
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = (String[]) pPlugins.get(plugin);
            for (int j = 0; j < goals.length; j++) {
                final String goal = goals[j];
                //TODO: goal tree node should be moved to a common package (possible common/ui/goals/tree?)
                final MutableTreeNode goalNode = new GoalTreeNode(goal);
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            root.insert(pluginNode, root.getChildCount());
        }
    }
}
