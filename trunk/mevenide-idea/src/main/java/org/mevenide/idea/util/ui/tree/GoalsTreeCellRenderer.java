package org.mevenide.idea.util.ui.tree;

import org.mevenide.idea.util.ui.images.Icons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 * @author Arik
 */
public class GoalsTreeCellRenderer extends DefaultTreeCellRenderer
{
    @Override public Component getTreeCellRendererComponent(final JTree pTree,
                                                            final Object pNode,
                                                            final boolean pSelected,
                                                            final boolean pExpanded,
                                                            final boolean pLeaf,
                                                            final int pRow,
                                                            final boolean pHasFocus) {

        final Component comp = super.getTreeCellRendererComponent(pTree,
                                                                  pNode,
                                                                  pSelected,
                                                                  pExpanded,
                                                                  pLeaf,
                                                                  pRow,
                                                                  pHasFocus);
        if(comp instanceof JLabel) {
            final JLabel label = (JLabel) comp;
            if(pNode instanceof GoalTreeNode)
                label.setIcon(Icons.GOAL);
            else if(pNode instanceof PluginTreeNode)
                label.setIcon(Icons.PLUGIN);
        }

        return comp;
    }
}
