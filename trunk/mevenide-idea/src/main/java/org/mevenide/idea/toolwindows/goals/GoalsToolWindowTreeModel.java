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
package org.mevenide.idea.toolwindows.goals;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.idea.module.ModuleFavoriteGoalsChangedEvent;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.module.ModuleSettingsListener;
import org.mevenide.idea.module.PomSelectionChangedEvent;
import org.mevenide.idea.util.goals.GoalsHelper;
import org.mevenide.idea.util.ui.tree.AbstractTreeModel;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.ModuleTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.List;

/**
 * @author Arik
 */
public class GoalsToolWindowTreeModel extends AbstractTreeModel implements ModuleListener,
                                                                           ModuleSettingsListener
{
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(GoalsToolWindowTreeModel.class);

    /**
     * Creates an instance.
     */
    public GoalsToolWindowTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode());

        final ModuleManager mgr = ModuleManager.getInstance(pProject);
        mgr.addModuleListener(this);

        final Module[] modules = mgr.getModules();
        for (final Module module : modules)
            addModuleTreeNode(module, false);
    }

    protected void addModuleTreeNode(final Module pModule, final boolean pNotify) {
        final TreeNode node = findNode(root, new ModuleNodeVisitor(pModule), 1);
        if (node != null)
            return;

        //
        //register as a listener for module and goal changes
        //
        ModuleSettings.getInstance(pModule).addModuleSettingsListener(this);

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
     * @param pModule the module to remove
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
     * @param pModuleNode the node to refresh
     * @param pNotify     whether to notify the model listeners that the model has changed
     */
    protected void refreshModuleTreeNode(final ModuleTreeNode pModuleNode,
                                         final boolean pNotify) {
        if (pModuleNode == null)
            return;

        final Module module = pModuleNode.getModule();
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        //
        //clear existing children
        //
        pModuleNode.removeAllChildren();

        //
        //add module-specific goals (e.g. maven.xml)
        //
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile != null) {
            final File mavenXmlFile = new File(pomFile.getParentFile(), "maven.xml");
            if (mavenXmlFile.isFile()) {
                final ProjectGoalsGrabber moduleGrabber = new ProjectGoalsGrabber();
                moduleGrabber.setMavenXmlFile(mavenXmlFile.getAbsolutePath());
                try {
                    moduleGrabber.refresh();
                    pModuleNode.insert(createGoalsGrabberNode(moduleGrabber),
                                       pModuleNode.getChildCount());
                }
                catch (Exception e) {
                    pModuleNode.insert(new DefaultMutableTreeNode(e.getMessage()),
                                       pModuleNode.getChildCount());
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        //
        //create the Favorites node
        //
        final IGoalsGrabber favoritesGrabber = moduleSettings.getFavoriteGoals();
        pModuleNode.insert(createGoalsGrabberNode(favoritesGrabber),
                           pModuleNode.getChildCount());

        //
        //notify model listeners that nodes changed, if requested to
        //
        if (pNotify)
            nodeStructureChanged(pModuleNode);
    }

    /**
     * Creates a tree node named after the {@link org.mevenide.goals.grabber.IGoalsGrabber#getName() goals grabber name}
     * with a child node for each plugin, and for each plugin node a node list of its
     * goals.
     *
     * @param pGoalsGrabber the grabber to introspect
     * @return a mutable tree node
     */
    protected MutableTreeNode createGoalsGrabberNode(final IGoalsGrabber pGoalsGrabber) {
        final MutableTreeNode grabberNode = new DefaultMutableTreeNode(pGoalsGrabber.getName());
        final String[] plugins = pGoalsGrabber.getPlugins();
        for (final String plugin : plugins) {
            final MutableTreeNode pluginNode = new PluginTreeNode(plugin);

            final String[] goals = pGoalsGrabber.getGoals(plugin);
            for (final String goal : goals) {
                final String fqGoalName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final String description = pGoalsGrabber.getDescription(fqGoalName);
                final String[] prereqs = pGoalsGrabber.getPrereqs(fqGoalName);
                final MutableTreeNode goalNode = new GoalTreeNode(goal, description, prereqs);
                pluginNode.insert(goalNode, pluginNode.getChildCount());
            }

            grabberNode.insert(pluginNode, grabberNode.getChildCount());
        }

        return grabberNode;
    }

    public void moduleAdded(final Project pProject, final Module pModule) {
        addModuleTreeNode(pModule, true);
    }

    public void beforeModuleRemoved(final Project pProject, final Module pModule) {
    }

    public void moduleRemoved(final Project pProject, final Module pModule) {
        removeModuleTreeNode(pModule, true);
    }

    public void modulesRenamed(final Project pProject, final List<Module> pModules) {
        for (final Module module : pModules) {
            final NodeVisitor visitor = new ModuleNodeVisitor(module);
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
        if (node != null)
            refreshModuleTreeNode((ModuleTreeNode) node, true);
    }

    public void moduleFavoriteGoalsChanged(final ModuleFavoriteGoalsChangedEvent pEvent) {

        //
        //find changed module
        //
        final Module module = pEvent.getSource().getModule();

        //
        //find that module in the tree model
        //
        final NodeVisitor visitor = new ModuleNodeVisitor(module);
        final TreeNode node = findNode(root, visitor);
        if (node != null)
            refreshModuleTreeNode((ModuleTreeNode) node, true);
    }

    private class ModuleNodeVisitor implements NodeVisitor
    {
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
