package org.mevenide.idea.main.windows.goals;

import com.intellij.execution.ExecutionException;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.Icons;
import com.intellij.util.ui.Tree;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.common.JdkNotDefinedException;
import org.mevenide.idea.common.MavenHomeNotDefinedException;
import org.mevenide.idea.common.PomNotDefinedException;
import org.mevenide.idea.common.actions.AbstractAnAction;
import org.mevenide.idea.common.ui.ConfigurableWrapper;
import org.mevenide.idea.common.ui.Images;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.util.Res;
import org.mevenide.idea.main.settings.module.ModuleSettingsConfigurable;
import org.mevenide.idea.main.windows.execution.ExecutionToolWindow;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arik
 */
public class GoalsToolWindow extends JPanel {
    private static final Log LOG = LogFactory.getLog(GoalsToolWindow.class);
    private static final Res RES = Res.getInstance(GoalsToolWindow.class);
    private static final String TITLE = RES.get("title");

    /**
     * The project this tool window belongs to.
     */
    private final Project project;

    /**
         * The goals tree. Used by {@link #getSelectedModule()} to find out to which module the selected
         * goal(s) belong.
         */
    private final Tree goalsTree = new Tree();

    /**
     * The goals model.
     */
    private GoalsTreeModel model;

    public GoalsToolWindow(final Project pProject) {
        project = pProject;
        init();
    }

    public GoalsToolWindow(final Project pProject, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        project = pProject;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        //
        // create the goals tree
        //
        model = new GoalsTreeModel(project);
        goalsTree.setModel(model);
        goalsTree.setRootVisible(false);
        goalsTree.setShowsRootHandles(true);
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
                        runGoals(new String[]{plugin + ":" + goal});
                    }
                }
            }
        });

        //
        // create the action toolbar
        //
        final GoalsTreeExpanded expander = new GoalsTreeExpanded();
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AttainGoalAction());
        actionGroup.add(new ShowModuleSettingsAction());
        actionGroup.addSeparator();
        actionGroup.add(CommonActionsManager.getInstance().createCollapseAllAction(expander));
        actionGroup.add(CommonActionsManager.getInstance().createExpandAllAction(expander));
        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
                        TITLE,
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
        add(new JScrollPane(goalsTree), c);
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

        final List goalList = new ArrayList(selections.length);

        for (int i = 0; i < selections.length; i++) {
            final TreePath selection = selections[i];
            final TreeNode node = (TreeNode) selection.getLastPathComponent();
            if (node instanceof GoalTreeNode) {
                final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                final String plugin = pluginNode.getPlugin();
                final String goal = ((GoalTreeNode) node).getGoal();
                goalList.add(plugin + ":" + goal);
            }
        }

        return (String[]) goalList.toArray(new String[goalList.size()]);
    }

    public void runSelectedGoals() {
        runGoals(getSelectedGoals());
    }

    public void runGoals(final String[] pGoals) {
        try {
            final ExecutionToolWindow execTw = ExecutionToolWindow.getInstance(project);
            execTw.runMaven(getSelectedModule(), pGoals);
        }
        catch (JdkNotDefinedException e) {
            Messages.showErrorDialog(project, e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (PomNotDefinedException e) {
            Messages.showErrorDialog(project, e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (MavenHomeNotDefinedException e) {
            Messages.showErrorDialog(project, e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (ExecutionException e) {
            Messages.showErrorDialog(project, e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }
    }

    public static void register(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        final GoalsToolWindow toolWin = new GoalsToolWindow(project);
        toolMgr.registerToolWindow(GoalsToolWindow.TITLE, toolWin, ToolWindowAnchor.RIGHT);
        final ToolWindow goalsTw = toolMgr.getToolWindow(GoalsToolWindow.TITLE);
        goalsTw.setIcon(new ImageIcon(Images.MAVEN_ICON));
    }

    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(TITLE);
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

    private class AttainGoalAction extends AbstractAnAction {
        public AttainGoalAction() {
            super(RES.get("attain.goal.action.text"),
                  RES.get("attain.goal.action.desc"),
                  new ImageIcon(Images.PLAY));
        }

        public void update(AnActionEvent e) {
            final String[] goals = getSelectedGoals();
            e.getPresentation().setEnabled(goals != null && goals.length > 0);
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            runSelectedGoals();
        }
    }

    private class ShowModuleSettingsAction extends AbstractAnAction {
        public ShowModuleSettingsAction() {
            super(RES.get("show.settings.action.text"),
                  RES.get("show.settings.action.desc"),
                  new ImageIcon(Images.OPTIONS));
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
                    wrapper.setCustomIcon(Icons.WEB_ICON);
                else if (module.getModuleType().equals(ModuleType.EJB))
                    wrapper.setCustomIcon(Icons.EJB_ICON);
                else if (module.getModuleType().equals(ModuleType.J2EE_APPLICATION))
                    wrapper.setCustomIcon(Icons.PACKAGE_ICON);
                else if (module.getModuleType().equals(ModuleType.JAVA))
                    wrapper.setCustomIcon(Icons.CLASS_ICON);

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
