package org.mevenide.idea.synchronize.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class FixProblemsAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(FixProblemsAction.class);

    private final JTree problemsTree;

    public FixProblemsAction(final JTree pProblemsTree) {
        super(RES.get("fix.problems.action.title"),
              RES.get("fix.problems.action.desc"),
              Icons.FIX_PROBLEMS);

        problemsTree = pProblemsTree;
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        if (pEvent == null || pEvent.getPresentation() == null)
            return;

        if (problemsTree == null)
            pEvent.getPresentation().setEnabled(false);
        else {
            final TreePath[] selectedPaths = problemsTree.getSelectionPaths();
            if (selectedPaths == null)
                pEvent.getPresentation().setEnabled(false);
            else {
                for (TreePath path : selectedPaths) {
                    Object value = path.getLastPathComponent();
                    if (!(value instanceof DefaultMutableTreeNode)) {
                        pEvent.getPresentation().setEnabled(false);
                        return;
                    }

                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    value = node.getUserObject();
                    if (!(value instanceof ProblemInfo)) {
                        pEvent.getPresentation().setEnabled(false);
                        return;
                    }

                    final ProblemInfo problem = (ProblemInfo) value;
                    if (!problem.canBeFixed()) {
                        pEvent.getPresentation().setEnabled(false);
                        return;
                    }
                }

                pEvent.getPresentation().setEnabled(
                    selectedPaths != null && selectedPaths.length > 0);
            }
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final TreePath[] selectedPaths = problemsTree.getSelectionPaths();
        for (TreePath path : selectedPaths) {
            Object value = path.getLastPathComponent();
            if (!(value instanceof DefaultMutableTreeNode))
                continue;

            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            value = node.getUserObject();
            if (!(value instanceof ProblemInfo))
                continue;

            final ProblemInfo problem = (ProblemInfo) value;
            if (!problem.canBeFixed())
                continue;

            problem.fix();
        }
    }
}
