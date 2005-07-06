package org.mevenide.idea.project.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.mevenide.idea.project.model.PluginInfo;

/**
 * @author Arik
 */
public class PluginNode extends DefaultMutableTreeNode {
    public PluginNode(final PluginInfo pPluginInfo) {
        super(pPluginInfo);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public PluginInfo getUserObject() {
        return (PluginInfo) super.getUserObject();
    }
}
