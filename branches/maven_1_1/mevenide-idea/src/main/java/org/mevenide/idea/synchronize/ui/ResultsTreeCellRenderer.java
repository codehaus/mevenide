package org.mevenide.idea.synchronize.ui;

import com.intellij.openapi.actionSystem.AnAction;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class ResultsTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object pValue,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {

        final String text;

        final ProblemInfo problem = getNodeProblem(pValue);
        if (problem != null)
            text = problem.getDescription();
        else
            text = pValue == null ? "" : pValue.toString();

        final Component c = super.getTreeCellRendererComponent(tree,
                                                               text,
                                                               sel,
                                                               expanded,
                                                               leaf,
                                                               row,
                                                               hasFocus);
        if (c instanceof JLabel && problem != null) {
            final JLabel label = (JLabel) c;
            final AnAction[] fixActions = problem.getFixActions();

            if (!problem.isValid())
                label.setIcon(Icons.PROBLEM_FIXED);
            else if (fixActions != null && fixActions.length > 0)
                label.setIcon(Icons.PROBLEM);
            else
                label.setIcon(Icons.WARNING);
        }

        return c;
    }

    private ProblemInfo getNodeProblem(final Object pNode) {
        if (!(pNode instanceof DefaultMutableTreeNode))
            return null;

        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) pNode;
        final Object userObject = node.getUserObject();
        if (!(userObject instanceof ProblemInfo))
            return null;

        return (ProblemInfo) userObject;
    }
}
