/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
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
