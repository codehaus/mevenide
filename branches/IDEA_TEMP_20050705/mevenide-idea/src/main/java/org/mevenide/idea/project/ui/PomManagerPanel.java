package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Tree;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.actions.AddGoalToPomAction;
import org.mevenide.idea.project.actions.ExecuteGoalAction;
import org.mevenide.idea.project.actions.RefreshPomToolWindowAction;
import org.mevenide.idea.project.actions.RemoveGoalFromPomAction;
import org.mevenide.idea.project.model.GoalInfo;

/**
 * @author Arik
 */
public class PomManagerPanel extends JPanel implements Disposable {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PomManagerPanel.class);

    /**
     * Tool window name.
     */
    public static final String TITLE = RES.get("pom.manager.name");

    /**
     * The project this instance is registered for.
     */
    private final Project project;

    /**
     * The Maven tree model.
     */
    private final PomTreeModel model;
    private final JTree tree;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this instance will be registered to
     */
    public PomManagerPanel(final Project pProject) {
        super(new BorderLayout());

        project = pProject;
        model = new PomTreeModel(project);
        tree = new Tree(model);

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new PomManagerTreeCellRenderer());
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        final DefaultActionGroup actionGrp = new DefaultActionGroup("POM Manager", false);
        actionGrp.add(new ExecuteGoalAction());
        actionGrp.add(new AddGoalToPomAction());
        actionGrp.add(new RemoveGoalFromPomAction());
        actionGrp.add(new RefreshPomToolWindowAction());
        final ActionManager actionMgr = ActionManager.getInstance();
        final ActionToolbar toolbar = actionMgr.createActionToolbar(TITLE, actionGrp, true);
        add(toolbar.getComponent(), BorderLayout.PAGE_START);
    }

    public Project getProject() {
        return project;
    }

    public void refresh() {
        model.refresh();
    }

    public VirtualFile[] getSelectedProjects() {
        return getSelectedProjects(true);
    }

    public VirtualFile[] getSelectedProjects(final boolean pStrict) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new VirtualFile[0];

        final Set<PomNode> projects = new HashSet<PomNode>(3);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (item instanceof PomNode)
                projects.add(((PomNode) item));
            else if (!pStrict && item instanceof GoalNode) {
                final GoalNode node = (GoalNode) item;
                final PomNode pomParent = getProjectForNode(node);
                if (pomParent != null)
                    projects.add(pomParent);
            }
        }

        final int size = projects.size();
        final PomNode[] nodes = projects.toArray(new PomNode[size]);
        final VirtualFile[] files = new VirtualFile[projects.size()];
        for (int i = 0; i < nodes.length; i++)
            files[i] = nodes[i].getUserObject().getFile();

        return files;
    }

    public GoalInfo[] getSelectedGoals(final VirtualFile pPomFile) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new GoalInfo[0];

        final Set<GoalInfo> goals = new HashSet<GoalInfo>(selection.length);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (!(item instanceof GoalNode))
                continue;

            final GoalNode node = (GoalNode) item;
            final GoalInfo goal = node.getUserObject();
            final PomNode pomNode = getProjectForNode(node);

            if (pomNode == null && pPomFile == null)
                goals.add(goal);

            else if (pPomFile != null && pomNode != null &&
                    pPomFile.equals(pomNode.getUserObject().getFile()))
                goals.add(goal);
        }

        return goals.toArray(new GoalInfo[goals.size()]);
    }

    public void dispose() {
        model.dispose();
    }

    private PomNode getProjectForNode(final TreeNode pNode) {
        TreeNode parent = pNode;
        while (parent != null && !(parent instanceof PomNode)) parent = parent.getParent();

        return (PomNode) parent;
    }
}
