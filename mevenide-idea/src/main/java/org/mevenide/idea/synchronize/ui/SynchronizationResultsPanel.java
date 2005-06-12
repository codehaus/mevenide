package org.mevenide.idea.synchronize.ui;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.util.ui.Tree;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.PopupHandler;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.InspectProjectAction;
import org.mevenide.idea.util.ui.images.Icons;
import java.awt.BorderLayout;

/**
 * @todo allow grouping problems by module and/or type
 * @todo add "Fix All" action
 * @todo add "Close" action
 * 
 * @author Arik
 */
public class SynchronizationResultsPanel extends JPanel {

    public static final String NAME = "POM Sync";

    private final Project project;
    private final JTree tree;
    private final DefaultActionGroup actionGroup;

    public SynchronizationResultsPanel(final Project pProject,
                                       final ProblemInfo[] pProblems) {
        project = pProject;
        tree = new Tree();
        actionGroup = new DefaultActionGroup();
        final InspectProjectAction rerunAction = new InspectProjectAction();
        rerunAction.getTemplatePresentation().setIcon(Icons.RERUN);
        actionGroup.add(rerunAction);
        actionGroup.add(new FixProblemsAction(tree));

        setProblems(pProblems);
        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        final ActionManager actMgr = ActionManager.getInstance();
        final ActionToolbar toolbar = actMgr.createActionToolbar(NAME, actionGroup, false);
        PopupHandler.installPopupHandler(tree, actionGroup, "tree", actMgr);

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
}
