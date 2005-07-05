package org.mevenide.idea.project.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mevenide.idea.util.ui.images.Icons;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;

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
        if(c instanceof JLabel) {
            final JLabel label = (JLabel) c;

            if (pValue instanceof PomNode) {
                final PomNode pomNode = (PomNode) pValue;
                final VirtualFilePointer filePointer = pomNode.getUserObject();

                label.setText(filePointer.getPresentableUrl());
                label.setIcon(Icons.MAVEN);

                if(!filePointer.isValid())
                    label.setForeground(Color.RED);
            }
        }

        return c;
    }
}
