package org.mevenide.idea.project.ui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class PomNode extends DefaultMutableTreeNode {
    public PomNode(final String pPomUrl) {
        super(pPomUrl);
    }

    @Override
    public String getUserObject() {
        return (String) super.getUserObject();
    }
}
