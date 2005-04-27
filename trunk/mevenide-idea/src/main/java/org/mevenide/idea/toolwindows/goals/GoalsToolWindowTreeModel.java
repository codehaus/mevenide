package org.mevenide.idea.toolwindows.goals;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.GoalsChangedEvent;
import org.mevenide.idea.GoalsProviderListener;
import org.mevenide.idea.module.ModuleGoalsProvider;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.module.ModuleSettingsListener;
import org.mevenide.idea.module.PomSelectionChangedEvent;
import org.mevenide.idea.util.ui.tree.AbstractTreeModel;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.ModuleTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;
import org.mevenide.idea.util.goals.GoalsHelper;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * @author Arik
 */
public class GoalsToolWindowTreeModel extends AbstractTreeModel implements ModuleListener,
                                                                           ModuleSettingsListener,
                                                                           GoalsProviderListener {
    /**
     * Creates an instance.
     */
    public GoalsToolWindowTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode());

        final ModuleManager mgr = ModuleManager.getInstance(pProject);
        mgr.addModuleListener(this);

        final Module[] modules = mgr.getModules();
        for (int i = 0; i < modules.length; i++)
            addModuleTreeNode(modules[i], false);
    }

    protected void addModuleTreeNode(final Module pModule, final boolean pNotify) {
        final TreeNode node = findNode(root, new ModuleNodeVisitor(pModule), 1);
        if (node != null)
            return;

        //
        //register as a listener for module and goal changes
        //
        ModuleSettings.getInstance(pModule).addModuleSettingsListener(this);
        ModuleGoalsProvider.getInstance(pModule).addGoalsProviderListener(this);

        //
        //create module node
        //
        final MutableTreeNode root = getMutableRoot();
        final ModuleTreeNode moduleNode = new ModuleTreeNode(pModule);
        root.insert(moduleNode, root.getChildCount());
        if (pNotify)
            nodesWereInserted(root, new int[]{root.getIndex(moduleNode)});

        //
        //populate the new node with goals
        //
        refreshModuleTreeNode(moduleNode, pNotify);
    }

    /**
     * Removes the given module from the goals tree.
     *
     * @param pModule      the module to remove
     * @param pNotify whether to notify the model listeners that the model has changed
     */
    protected void removeModuleTreeNode(final Module pModule,
                                        final boolean pNotify) {
        final TreeNode node = findNode(root, new ModuleNodeVisitor(pModule), 1);
        if (node == null)
            return;

        //
        //unregister ourselfs as listeners for module and goal changes
        //
        ModuleSettings.getInstance(pModule).removeModuleSettingsListener(this);
        ModuleGoalsProvider.getInstance(pModule).removeGoalsProviderListener(this);

        //
        //remove the node
        //
        final MutableTreeNode root = getMutableRoot();
        final int oldIndex = root.getIndex(node);
        root.remove((MutableTreeNode) node);

        //
        //notify listeners if requested
        //
        if (pNotify)
            nodesWereRemoved(root, new int[]{oldIndex}, new Object[]{node});
    }

    /**
     * Refreshes the given tree node contents.
     *
     * @param pNode   the node to refresh
     * @param pNotify whether to notify the model listeners that the model has changed
     */
    protected void refreshModuleTreeNode(final ModuleTreeNode pNode,
                                         final boolean pNotify) {
        if (pNode == null)
            return;

        final Module module = pNode.getModule();

        //
        //clear existing children
        //
        pNode.removeAllChildren();

        //
        //iterate over available module plugins and goals
        //
        final ModuleGoalsProvider goalsProvider = ModuleGoalsProvider.getInstance(module);
        final String[] plugins = goalsProvider.getPlugins();
        for (int i = 0; i < plugins.length; i++) {

            final String plugin = plugins[i];
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = goalsProvider.getGoals(plugin);
            for (int j = 0; j < goals.length; j++) {
                final String goal = goals[j];
                //TODO: isn't 'goal' already fq?
                final String fqGoalName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final String description = goalsProvider.getDescription(fqGoalName);
                final String[] prereqs = goalsProvider.getPrereqs(fqGoalName);
                final MutableTreeNode goalNode = new GoalTreeNode(goal, description, prereqs);
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            pNode.insert(pluginNode, pNode.getChildCount());
        }

        if (pNotify)
            nodeStructureChanged(pNode);
    }

    public void moduleAdded(final Project pProject, final Module pModule) {
        addModuleTreeNode(pModule, true);
    }

    public void beforeModuleRemoved(final Project pProject, final Module pModule) {
    }

    public void moduleRemoved(final Project pProject, final Module pModule) {
        removeModuleTreeNode(pModule, true);
    }

    public void modulesRenamed(final Project pProject, final List pModules) {
        final Module[] modules = (Module[]) pModules.toArray(new Module[pModules.size()]);
        for (int i = 0; i < modules.length; i++) {
            final NodeVisitor visitor = new ModuleNodeVisitor(modules[i]);
            final TreeNode node = findNode(root, visitor, 1);
            if (node != null)
                nodeChanged(node);
        }
    }

    public void modulePomSelectionChanged(final PomSelectionChangedEvent pEvent) {
        //
        //find changed module
        //
        final ModuleSettings moduleSettings = pEvent.getModuleSettings();
        final Module module = moduleSettings.getModule();

        //
        //find that module in the tree model
        //
        final NodeVisitor visitor = new ModuleNodeVisitor(module);
        final TreeNode node = findNode(root, visitor, 1);
        if(node != null)
            refreshModuleTreeNode((ModuleTreeNode) node, true);
    }

    public void goalsChanged(final GoalsChangedEvent pEvent) {
        //
        //find changed module
        //
        final ModuleGoalsProvider provider = (ModuleGoalsProvider) pEvent.getGoalsProvider();
        final Module module = provider.getModule();

        //
        //find that module in the tree model
        //
        final NodeVisitor visitor = new ModuleNodeVisitor(module);
        final TreeNode node = findNode(root, visitor);
        if (node != null)
            refreshModuleTreeNode((ModuleTreeNode) node, true);
    }

    private class ModuleNodeVisitor implements NodeVisitor {
        private final Module module;

        public ModuleNodeVisitor(final Module pModule) {
            module = pModule;
        }

        public boolean accept(TreeNode pNode) {
            if (pNode instanceof ModuleTreeNode) {
                final ModuleTreeNode node = (ModuleTreeNode) pNode;
                return node.getModule().equals(module);
            }
            return false;
        }
    }
}
