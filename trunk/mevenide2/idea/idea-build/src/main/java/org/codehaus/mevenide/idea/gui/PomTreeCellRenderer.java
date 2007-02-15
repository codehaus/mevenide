/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.gui;

import org.codehaus.mevenide.idea.config.NameDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.PluginGoal;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PomTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon goalIcon;
    private Icon pomIcon;

    /**
     * Constructs ...
     */
    public PomTreeCellRenderer() {
        super();
    }

    /**
     * Constructs ...
     *
     * @param pomIcon  Document me!
     * @param goalIcon Document me!
     */
    public PomTreeCellRenderer(Icon pomIcon, Icon goalIcon) {
        this.goalIcon = goalIcon;
        this.pomIcon = pomIcon;
    }

    /**
     * Method description
     *
     * @param tree     Document me!
     * @param value    Document me!
     * @param sel      Document me!
     * @param expanded Document me!
     * @param leaf     Document me!
     * @param row      Document me!
     * @param hasFocus Document me!
     *
     * @return Document me!
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if ((leaf && (node.getUserObject() instanceof NameDocument.Name.Enum))
                || (node.getUserObject() instanceof PluginGoal)) {
            setIcon(goalIcon);
        } else if (isMavenProject(node)) {
            MavenProjectDocument document = ((MavenProjectDocument) node.getUserObject());
            String tooltipText = document.getPomFile().getPath();

            setText(node.getUserObject().toString() + " (" + document.getPomFile().getPath() + ")");
            setToolTipText(tooltipText);
            setIcon(pomIcon);
        } else {
            setToolTipText(null);    // no tool tip
        }

        return this;
    }

    private boolean isMavenProject(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof MavenProjectDocument;
    }
}
