package org.mevenide.idea.goalstoolwindow;

import com.intellij.execution.ExecutionException;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.JdkNotDefinedException;
import org.mevenide.idea.MavenHomeNotDefinedException;
import org.mevenide.idea.PomNotDefinedException;
import org.mevenide.idea.runner.console.ExecutionToolWindow;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.util.Res;
import org.mevenide.idea.util.images.Images;

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
    private static final Res RES = Res.getInstance(GoalsToolWindow.class);
    private static final Log LOG = LogFactory.getLog(GoalsToolWindow.class);

    /**
     * The tool window title. Currently this is set to <code>null</code> -
     * see {@link #NAME} for details.
     */
    public static final String TITLE = null;

    /**
     * The unique tool window name. Basically, this <b>should</b> be something
     * like a class name or something that would always be unique. However,
     * IntelliJ also uses this for the user-friendly title of the tool window,
     * and hence, we need to actually set our title here, since it's visible
     * to the user.
     */
    public static final String NAME = RES.get("title");

    /**
     * The project this tool window belongs to.
     */
    private final Project project;

    /**
     * The goals tree. Used by {@link #getSelectedModule()} to find out
     * to which module the selected goal(s) belong.
     */
    private Tree goalsTree;
    private GoalsToolWindowTreeModel model;

    public GoalsToolWindow(final Project pProject) {
        project = pProject;
        init();
    }

    public GoalsToolWindow(final Project pProject,
                           boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        project = pProject;
        init();
    }

    protected void init() {
        removeAll();

        setLayout(new GridBagLayout());
        GridBagConstraints c;

        //
        // create the goals tree
        //
        model = new GoalsToolWindowTreeModel(project);
        goalsTree = new Tree(model);
        goalsTree.setRootVisible(false);
        goalsTree.setShowsRootHandles(true);
        goalsTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent pEvent) {
                if(pEvent.getClickCount() == 2) {
                    final int row = goalsTree.getRowForLocation(pEvent.getX(), pEvent.getY());
                    if(row < 0)
                        return;

                    final TreePath path = goalsTree.getPathForRow(row);
                    if(path == null)
                        return;

                    final TreeNode node = (TreeNode) path.getLastPathComponent();
                    if(node instanceof GoalTreeNode) {
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
        add(new JScrollPane(goalsTree), c);
    }

    public Module getSelectedModule() {
        final TreePath selection = goalsTree.getSelectionPath();
        if(selection == null)
            return null;

        final TreeNode moduleNode = (TreeNode) selection.getPathComponent(1);
        if(moduleNode instanceof ModuleTreeNode)
            return ((ModuleTreeNode) moduleNode).getModule();
        else
            return null;
    }

    public String[] getSelectedGoals() {
        final TreePath[] selections = goalsTree.getSelectionPaths();
        if(selections == null || selections.length == 0)
            return new String[0];

        final List goalList = new ArrayList(selections.length);

        for (int i = 0; i < selections.length; i++) {
            final TreePath selection = selections[i];
            final TreeNode node = (TreeNode) selection.getLastPathComponent();
            if(node instanceof GoalTreeNode) {
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
            Messages.showErrorDialog(project, e.getMessage(), UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (PomNotDefinedException e) {
            Messages.showErrorDialog(project, e.getMessage(), UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (MavenHomeNotDefinedException e) {
            Messages.showErrorDialog(project, e.getMessage(), UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }
        catch (ExecutionException e) {
            Messages.showErrorDialog(project, e.getMessage(), UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }
    }

    public static GoalsToolWindow getInstance(final Project pProject) {
        final ToolWindow toolWindow = ToolWindowManager.getInstance(pProject).getToolWindow(NAME);
        return (GoalsToolWindow) toolWindow.getComponent();
    }

    private class GoalsTreeExpanded implements TreeExpander {

        public boolean canCollapse() {
            final TreeNode root = (TreeNode) model.getRoot();
            if(goalsTree.isCollapsed(new TreePath(root)))
                return false;

            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                if(goalsTree.isExpanded(new TreePath(nodes)))
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

    private class AttainGoalAction extends AnAction {
        public AttainGoalAction() {
            super(RES.get("attain.goal.action.text"),
                  RES.get("attain.goal.action.desc"),
                  new ImageIcon(Images.PLAY));
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            runSelectedGoals();
        }
    }

}
