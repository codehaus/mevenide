package org.mevenide.idea.main.windows.goals;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.common.settings.module.FavoriteGoalsChangedEvent;
import org.mevenide.idea.common.settings.module.ModuleSettingsListener;
import org.mevenide.idea.common.settings.module.PomFileChangedEvent;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.util.GoalsHelper;
import org.mevenide.idea.main.settings.module.ModuleSettings;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * @author Arik
 */
public class GoalsTreeModel extends DefaultTreeModel {
    private static final Log LOG = LogFactory.getLog(GoalsTreeModel.class);

    /**
     * Listens the module additions/removals and updates the model.
     */
    private final TreeModuleListener moduleListener = new TreeModuleListener();

    /**
     * Listens to module settings changes and updates the model as necessary.
     */
    private final TreeModuleSettingsListener moduleSettingsListener = new TreeModuleSettingsListener();

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the IDEA project
     */
    public GoalsTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode());

        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        for (int i = 0; i < modules.length; i++)
            addModuleTreeNode(modules[i], false);

        ModuleManager.getInstance(pProject).addModuleListener(moduleListener);
    }

    /**
     * Adds the given module to the goals tree.
     *
     * @param pModule the module to add
     * @param pNotifyModel whether to notify the model listeners that the model has changed
     */
    protected void addModuleTreeNode(final Module pModule,
                                     final boolean pNotifyModel) {
        final ModuleTreeNode node = findModuleNode(pModule);
        if (node != null)
            return;

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        moduleSettings.addModuleSettingsListener(moduleSettingsListener);

        final MutableTreeNode root = (MutableTreeNode) super.root;
        final ModuleTreeNode moduleNode = new ModuleTreeNode(pModule);
        root.insert(moduleNode, root.getChildCount());
        if (pNotifyModel)
            nodesWereInserted(root, new int[]{root.getIndex(moduleNode)});

        refreshModuleTreeNode(pModule, pNotifyModel);
    }

    /**
     * Removes the given module from the goals tree.
     *
     * @param pModule the module to remove
     * @param pNotifyModel whether to notify the model listeners that the model has changed
     */
    protected void removeModuleTreeNode(final Module pModule,
                                        final boolean pNotifyModel) {
        final ModuleTreeNode node = findModuleNode(pModule);
        if (node == null)
            return;

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        moduleSettings.removeModuleSettingsListener(moduleSettingsListener);

        final MutableTreeNode root = (MutableTreeNode) super.root;

        final int oldIndex = root.getIndex(node);
        root.remove(node);

        if (pNotifyModel)
            nodesWereRemoved(root, new int[]{oldIndex}, new Object[]{node});
    }

    /**
     * Finds the tree node for the given module.
     *
     * @param pModule the module to search for
     * @return the appropriate tree node, or <code>null</code> if not found
     */
    protected ModuleTreeNode findModuleNode(final Module pModule) {
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final TreeNode node = root.getChildAt(i);
            if (node instanceof ModuleTreeNode) {
                final ModuleTreeNode moduleNode = (ModuleTreeNode) node;
                if (moduleNode.getModule().equals(pModule))
                    return moduleNode;
            }
        }

        return null;
    }

    /**
     * Refreshes the tree node contents of the given module.
     *
     * @param pModule the module to refresh
     * @param pNotifyModel whether to notify the model listeners that the model has changed
     */
    protected void refreshModuleTreeNode(final Module pModule,
                                         final boolean pNotifyModel) {
        refreshModuleTreeNode(findModuleNode(pModule), pNotifyModel);
    }

    /**
     * Refreshes the given tree node contents.
     *
     * @param pNode the node to refresh
     * @param pNotifyModel whether to notify the model listeners that the model has changed
     */
    protected void refreshModuleTreeNode(final ModuleTreeNode pNode,
                                         final boolean pNotifyModel) {
        if (pNode == null)
            return;

        final Module module = pNode.getModule();
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        try {
            pNode.removeAllChildren();
            final String[] plugins = moduleSettings.getPlugins();
            for (int i = 0; i < plugins.length; i++) {

                final String plugin = plugins[i];
                final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

                final String[] goals = moduleSettings.getGoals(plugin);
                for (int j = 0; j < goals.length; j++) {
                    final String goal = goals[j];
                    final String fqGoalName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                    final String description = moduleSettings.getDescription(fqGoalName);
                    final MutableTreeNode goalNode = new GoalTreeNode(goal, description);
                    pluginNode.insert(goalNode, pluginNode.getChildCount());
                }

                pNode.insert(pluginNode, pNode.getChildCount());
            }
        }
        catch (Exception e) {
            Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }

        if (pNotifyModel)
            nodeStructureChanged(pNode);
    }

    private class TreeModuleSettingsListener implements ModuleSettingsListener {
        public void favoriteGoalsChanged(FavoriteGoalsChangedEvent pEvent) {
            final Module module = pEvent.getModuleSettings().getModule();
            refreshModuleTreeNode(module, true);
        }

        public void pomFileChanged(PomFileChangedEvent pEvent) {
            final Module module = pEvent.getModuleSettings().getModule();
            refreshModuleTreeNode(module, true);
        }
    }

    private class TreeModuleListener implements ModuleListener {
        public void beforeModuleRemoved(Project project, Module module) {
        }

        public void moduleAdded(Project pProject, Module pModule) {
            addModuleTreeNode(pModule, true);
        }

        public void moduleRemoved(Project pProject, Module pModule) {
            removeModuleTreeNode(pModule, true);
        }

        public void modulesRenamed(Project project, List pModules) {
            final Object[] modules = pModules.toArray();
            for (int i = 0; i < modules.length; i++) {
                final Module module = (Module) modules[i];
                final ModuleTreeNode node = findModuleNode(module);
                if (node == null)
                    continue;

                nodeChanged(node);
            }
        }
    }
}
