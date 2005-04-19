package org.mevenide.idea.main.windows.goals;

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