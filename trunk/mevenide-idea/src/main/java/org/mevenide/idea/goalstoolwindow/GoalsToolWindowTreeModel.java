package org.mevenide.idea.goalstoolwindow;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.support.ui.AbstractTreeModel;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.settings.module.ModuleSettingsListener;
import org.mevenide.idea.settings.module.PomFileChangedEvent;
import org.mevenide.idea.settings.module.ModuleSettings;
import org.mevenide.idea.settings.module.FavoriteGoalsChangedEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * @author Arik
 */
public class GoalsToolWindowTreeModel extends AbstractTreeModel implements ModuleListener,ModuleSettingsListener {
    private static final Log LOG = LogFactory.getLog(GoalsToolWindowTreeModel.class);

    public GoalsToolWindowTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode());

        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        for (int i = 0; i < modules.length; i++)
            addModuleTreeNode(modules[i], false);

        ModuleManager.getInstance(pProject).addModuleListener(this);
    }

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

    protected void addModuleTreeNode(final Module pModule) {
        addModuleTreeNode(pModule, true);
    }

    protected void addModuleTreeNode(final Module pModule,
                                     final boolean pNotifyModel) {
        final ModuleTreeNode node = findModuleNode(pModule);
        if (node != null)
            return;

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        moduleSettings.addModuleSettingsListener(this);

        final MutableTreeNode root = getMutableRoot();
        final ModuleTreeNode moduleNode = new ModuleTreeNode(pModule);
        root.insert(moduleNode, root.getChildCount());
        if(pNotifyModel)
            nodesWereInserted(root, new int[]{root.getIndex(moduleNode)});

        refreshModuleTreeNode(pModule, pNotifyModel);
    }

    protected void refreshModuleTreeNode(final Module pModule,
                                         final boolean pNotifyModel) {
        refreshModuleTreeNode(findModuleNode(pModule), pNotifyModel);
    }

    protected void refreshModuleTreeNode(final ModuleTreeNode pNode,
                                         final boolean pNotifyModel) {
        if(pNode == null)
            return;

        final Module module = pNode.getModule();
        final IGoalsGrabber goalsGrabber = new ModuleGoalsGrabber(module);

        try {
            goalsGrabber.refresh();

            pNode.removeAllChildren();
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

                pNode.insert(pluginNode, pNode.getChildCount());
            }
        }
        catch (Exception e) {
            Messages.showErrorDialog(module.getProject(),
                                     e.getMessage(),
                                     UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }

        if (pNotifyModel)
            nodeStructureChanged(pNode);
    }

    protected void removeModuleTreeNode(final Module pModule,
                                        final boolean pNotifyModel) {
        final ModuleTreeNode node = findModuleNode(pModule);
        if (node == null)
            return;

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        moduleSettings.removeModuleSettingsListener(this);

        final MutableTreeNode root = getMutableRoot();

        final int oldIndex = root.getIndex(node);
        root.remove(node);

        if (pNotifyModel)
            nodesWereRemoved(root, new int[]{oldIndex}, new Object[]{node});
    }

    public void beforeModuleRemoved(final Project pProject, final Module pModule) {
    }

    public void moduleAdded(final Project pProject, final Module pModule) {
        addModuleTreeNode(pModule, true);
    }

    public void moduleRemoved(final Project pProject, final Module pModule) {
        removeModuleTreeNode(pModule, true);
    }

    public void modulesRenamed(final Project pProject, final List pModules) {
        final Object[] modules = pModules.toArray();
        for (int i = 0; i < modules.length; i++) {
            final Module module = (Module) modules[i];
            final ModuleTreeNode node = findModuleNode(module);
            if (node == null)
                continue;

            nodeChanged(node);
        }
    }

    public void pomFileChanged(PomFileChangedEvent pEvent) {
        final Module module = pEvent.getModuleSettings().getModule();
        refreshModuleTreeNode(module, true);
    }

    public void favoriteGoalsChanged(FavoriteGoalsChangedEvent pEvent) {
        final Module module = pEvent.getModuleSettings().getModule();
        refreshModuleTreeNode(module, true);
    }
}
