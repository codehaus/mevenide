package org.mevenide.idea.synchronize.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Tree;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.InspectProjectAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 * @todo allow grouping problems by module and/or type
 */
public class SynchronizationResultsPanel extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SynchronizationResultsPanel.class);

    public static final String NAME = "POM Sync";

    private final Project project;
    private final JTree tree;
    private final DefaultActionGroup actionGroup;
    public static final String PLACE_PROBLEMS_TREE = "problemsTree";

    public SynchronizationResultsPanel(final Project pProject) {
        this(pProject, null);
    }

    public SynchronizationResultsPanel(final Project pProject,
                                       final ProblemInfo[] pProblems) {
        project = pProject;
        tree = new Tree();

        final InspectProjectAction rerunAction = new InspectProjectAction();
        rerunAction.getTemplatePresentation().setIcon(Icons.RERUN);

        actionGroup = new DefaultActionGroup();
        actionGroup.add(rerunAction);
        actionGroup.add(new CloseProblemsPaneAction());

        setProblems(pProblems == null ? new ProblemInfo[0] : pProblems);
        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        final ActionManager actMgr = ActionManager.getInstance();
        final ActionToolbar toolbar = actMgr.createActionToolbar(NAME,
                                                                 actionGroup,
                                                                 false);
        PopupHandler.installPopupHandler(tree,
                                         new ProblemActionGroup(),
                                         PLACE_PROBLEMS_TREE,
                                         actMgr);

        add(toolbar.getComponent(), BorderLayout.LINE_START);
    }

    public final void setProblems(final ProblemInfo[] pProblems) {
        final MutableTreeNode root = new DefaultMutableTreeNode(project);
        for (ProblemInfo problemInfo : pProblems) {
            final MutableTreeNode problemNode = new DefaultMutableTreeNode(problemInfo);
            root.insert(problemNode, root.getChildCount());
        }

        tree.setModel(new DefaultTreeModel(root));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setCellRenderer(new ResultsTreeCellRenderer());
        tree.setRowHeight(23);
    }

    public static ToolWindow getInstance(final Project pProject) {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(pProject);
        return mgr.getToolWindow(NAME);
    }

    private class ProblemActionGroup extends ActionGroup {
        public AnAction[] getChildren(final AnActionEvent pEvent) {
            if (!PLACE_PROBLEMS_TREE.equals(pEvent.getPlace()))
                return new AnAction[0];

            final TreePath selectedPath = tree.getSelectionPath();
            final Object lastPathComp = selectedPath.getLastPathComponent();
            if (!(lastPathComp instanceof DefaultMutableTreeNode))
                return new AnAction[0];

            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastPathComp;
            final Object userObject = node.getUserObject();
            if (!(userObject instanceof ProblemInfo))
                return new AnAction[0];

            final ProblemInfo problem = (ProblemInfo) userObject;
            final AnAction[] fixActions = problem.getFixActions();
            if (fixActions == null)
                return new AnAction[0];

            return fixActions;
        }
    }

    private class CloseProblemsPaneAction extends AbstractAnAction {
        public CloseProblemsPaneAction() {
            super(RES.get("close.problems.pane.text"),
                  RES.get("close.problems.pane.desc"),
                  Icons.CANCEL);
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            final Project project = getProject(pEvent);
            final ToolWindow tw = SynchronizationResultsPanel.getInstance(project);
            if (tw == null)
                return;

            tw.setAvailable(false, null);
        }
    }
}
