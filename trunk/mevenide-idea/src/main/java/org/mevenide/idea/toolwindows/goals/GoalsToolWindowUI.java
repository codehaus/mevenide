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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DataManager;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Tree;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.Res;
import org.mevenide.idea.actions.AbstractAnAction;
import org.mevenide.idea.execute.MavenRunner;
import org.mevenide.idea.module.ModuleSettingsConfigurable;
import org.mevenide.idea.util.ConfigurableWrapper;
import org.mevenide.idea.util.goals.GoalsHelper;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.GoalsTreeCellRenderer;
import org.mevenide.idea.util.ui.tree.ModuleTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;

/**
 * @author Arik
 */
public class GoalsToolWindowUI extends JPanel {

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(GoalsToolWindowUI.class);

    /**
     * The tool window title.
     */
    private static final String NAME = RES.get("title");

    /**
     * The goals tree. Used by {@link #getSelectedModule()} to find out to which module the selected goal(s)
     * belong.
     */
    private final Tree goalsTree = new Tree();

    /**
     * The tree model representing available goals and plugins.
     */
    private final GoalsToolWindowTreeModel model;


    public GoalsToolWindowUI(final Project pProject) {
        model = new GoalsToolWindowTreeModel(pProject);
        init();
    }

    public GoalsToolWindowUI(final Project pProject, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        model = new GoalsToolWindowTreeModel(pProject);
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        //
        // create the goals tree
        //
        goalsTree.setModel(model);
        goalsTree.setRootVisible(false);
        goalsTree.setShowsRootHandles(true);
        goalsTree.setCellRenderer(new GoalsTreeCellRenderer());
        goalsTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent pEvent) {
                if (pEvent.getClickCount() == 2) {
                    final int row = goalsTree.getRowForLocation(pEvent.getX(), pEvent.getY());
                    if (row < 0)
                        return;

                    final TreePath path = goalsTree.getPathForRow(row);
                    if (path == null)
                        return;

                    final TreeNode node = (TreeNode) path.getLastPathComponent();
                    if (node instanceof GoalTreeNode) {
                        final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                        final String plugin = pluginNode.getPlugin();
                        final String goal = ((GoalTreeNode) node).getGoal();
                        final String fqGoalName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                        final Component component = (Component) pEvent.getSource();
                        final DataContext dataContext = DataManager.getInstance().getDataContext(component,
                                                                                                 pEvent.getX(),
                                                                                                 pEvent.getY());
                        MavenRunner.execute(getSelectedModule(),
                                            new String[]{fqGoalName},
                                            dataContext);
                    }
                }
            }
        });

        //
        // create the action toolbar
        //
        final GoalsTreeExpanded expander = new GoalsTreeExpanded();
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AttainSelectedGoalsAction());
        actionGroup.add(new ShowModuleSettingsAction());
        actionGroup.addSeparator();
        actionGroup.add(CommonActionsManager.getInstance().createCollapseAllAction(expander));
        actionGroup.add(CommonActionsManager.getInstance().createExpandAllAction(expander));
        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
                        NAME,
                        actionGroup,
                        true);

        //
        // add components to layout
        //
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        add(toolbar.getComponent(), c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(ScrollPaneFactory.createScrollPane(goalsTree), c);
    }

    public Module getSelectedModule() {
        final TreePath selection = goalsTree.getSelectionPath();
        if (selection == null)
            return null;

        final TreeNode moduleNode = (TreeNode) selection.getPathComponent(1);
        if (moduleNode instanceof ModuleTreeNode)
            return ((ModuleTreeNode) moduleNode).getModule();
        else
            return null;
    }

    public String[] getSelectedGoals() {
        final TreePath[] selections = goalsTree.getSelectionPaths();
        if (selections == null || selections.length == 0)
            return new String[0];

        final List<String> goalList = new ArrayList<String>(selections.length);

        for (final TreePath selection : selections) {
            final TreeNode node = (TreeNode) selection.getLastPathComponent();
            if (node instanceof GoalTreeNode) {
                final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                final String plugin = pluginNode.getPlugin();
                final String goal = ((GoalTreeNode) node).getGoal();
                goalList.add(GoalsHelper.buildFullyQualifiedName(plugin, goal));
            }
        }

        return goalList.toArray(new String[goalList.size()]);
    }

    public static void register(final Project pProject) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(pProject);
        final GoalsToolWindowUI toolWin = new GoalsToolWindowUI(pProject);
        toolMgr.registerToolWindow(NAME, toolWin, ToolWindowAnchor.RIGHT);
        final ToolWindow goalsTw = toolMgr.getToolWindow(NAME);
        goalsTw.setIcon(Icons.MAVEN);
    }

    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(NAME);
    }

    public static GoalsToolWindowUI getInstance(final Project pProject) {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(pProject);
        final ToolWindow tw = mgr.getToolWindow(NAME);
        return (GoalsToolWindowUI) tw.getComponent();
    }

    private class GoalsTreeExpanded implements TreeExpander {
        public boolean canCollapse() {
            final TreeNode root = (TreeNode) model.getRoot();
            if (goalsTree.isCollapsed(new TreePath(root)))
                return false;

            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                if (goalsTree.isExpanded(new TreePath(nodes)))
                    return true;
            }

            return false;
        }

        public boolean canExpand() {
            final TreeNode root = (TreeNode) model.getRoot();
            if (goalsTree.isCollapsed(new TreePath(root)))
                return true;

            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                if (goalsTree.isCollapsed(new TreePath(nodes)))
                    return true;
            }

            return false;
        }

        public void collapseAll() {
            final TreeNode root = (TreeNode) model.getRoot();
            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                goalsTree.collapsePath(new TreePath(nodes));
            }
        }

        public void expandAll() {
            final TreeNode root = (TreeNode) model.getRoot();
            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                goalsTree.expandPath(new TreePath(nodes));
            }
        }
    }

    private class AttainSelectedGoalsAction extends AbstractAnAction {
        public AttainSelectedGoalsAction() {
            super(RES.get("attain.goal.action.text"),
                  RES.get("attain.goal.action.desc"),
                  Icons.EXECUTE);
        }

        public void update(AnActionEvent e) {
            final String[] goals = getSelectedGoals();
            e.getPresentation().setEnabled(goals != null && goals.length > 0);
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            MavenRunner.execute(getSelectedModule(),
                                getSelectedGoals(),
                                pEvent.getDataContext());
        }
    }

    private class ShowModuleSettingsAction extends AbstractAnAction {
        public ShowModuleSettingsAction() {
            super(RES.get("show.settings.action.text"),
                  RES.get("show.settings.action.desc"),
                  Icons.MAVEN_SETTINGS_SMALL);
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            final Project project = getProject(pEvent);

            final ConfigurableGroup[] configurableGroup =
                    new ConfigurableGroup[]{new ModulesConfigurableGroup(project)};
            ShowSettingsUtil.getInstance().showSettingsDialog(project, configurableGroup);
        }
    }

    private static class ModulesConfigurableGroup implements ConfigurableGroup {
        private final Configurable[] configurables;
        private static final String SETTINGS_MODULES_TITLE = RES.get("modules.title");

        public ModulesConfigurableGroup(final Project pProject) {
            final Module[] modules = ModuleManager.getInstance(pProject).getModules();
            configurables = new Configurable[modules.length];

            for (int i = 0; i < modules.length; i++) {

                final Module module = modules[i];
                final ModuleSettingsConfigurable configurable =
                        new ModuleSettingsConfigurable(module);

                final ConfigurableWrapper wrapper = new ConfigurableWrapper(configurable);
                wrapper.setCustomDisplayName(StringUtils.capitalize(module.getName()));

                if (module.getModuleType().equals(ModuleType.WEB))
                    wrapper.setCustomIcon(com.intellij.util.Icons.WEB_ICON);
                else if (module.getModuleType().equals(ModuleType.EJB))
                    wrapper.setCustomIcon(com.intellij.util.Icons.EJB_ICON);
                else if (module.getModuleType().equals(ModuleType.J2EE_APPLICATION))
                    wrapper.setCustomIcon(com.intellij.util.Icons.PACKAGE_ICON);
                else if (module.getModuleType().equals(ModuleType.JAVA))
                    wrapper.setCustomIcon(com.intellij.util.Icons.CLASS_ICON);

                configurables[i] = wrapper;
            }
        }

        public String getDisplayName() {
            return SETTINGS_MODULES_TITLE;
        }

        public String getShortName() {
            return SETTINGS_MODULES_TITLE;
        }

        public Configurable[] getConfigurables() {
            return configurables;
        }
    }
}
