package org.mevenide.idea.project.ui;

import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class PomManagerTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(final JTree pTree,
                                                  final Object pValue,
                                                  final boolean pSelected,
                                                  final boolean pExpanded,
                                                  final boolean pLead,
                                                  final int pRow,
                                                  final boolean pHasFocus) {

        final Component c = super.getTreeCellRendererComponent(pTree,
                                                               pValue,
                                                               pSelected,
                                                               pExpanded,
                                                               pLead,
                                                               pRow,
                                                               pHasFocus);
        if (c instanceof JLabel) {
            final JLabel label = (JLabel) c;

            if (pValue instanceof PomNode) {
                final PomNode pomNode = (PomNode) pValue;
                final VirtualFilePointer filePointer = pomNode.getUserObject();

                label.setText(filePointer.getPresentableUrl());
                label.setIcon(Icons.MAVEN);

                if (!filePointer.isValid())
                    label.setForeground(Color.RED);
            }
            else if (pValue instanceof PluginNode) {
                final PluginNode node = (PluginNode) pValue;
                final PluginInfo plugin = node.getUserObject();
                label.setText(plugin.getName() + " (" + plugin.getVersion() + ")");
                label.setIcon(Icons.PLUGIN);
            }
            else if (pValue instanceof GoalNode) {
                final GoalNode node = (GoalNode) pValue;
                final GoalInfo goal = node.getUserObject();
                label.setText(goal.getName());
                label.setIcon(Icons.GOAL);
            }
        }

        return c;
    }
}
