package org.mevenide.idea.util.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class PluginTreeNode extends DefaultMutableTreeNode {

    public PluginTreeNode(final String pPlugin) {
        super(pPlugin);
    }

    public String getUserObject() {
        return (String) super.getUserObject();
    }

    @Override public boolean isLeaf() {
        return false;
    }

    public String getPlugin() {
        return getUserObject();
    }
}