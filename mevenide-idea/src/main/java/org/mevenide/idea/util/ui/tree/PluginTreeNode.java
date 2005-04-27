package org.mevenide.idea.util.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class PluginTreeNode extends DefaultMutableTreeNode {

    public PluginTreeNode(final String pPlugin) {
        super(pPlugin);
    }

    public String getPlugin() {
        return (String) userObject;
    }
}