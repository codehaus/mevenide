package org.mevenide.idea.support.ui;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Arik
 */
public abstract class AbstractTreeModel extends DefaultTreeModel {

    protected AbstractTreeModel() {
        super(null);
    }

    protected AbstractTreeModel(TreeNode root) {
        super(root);
    }

    protected AbstractTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    protected MutableTreeNode getMutableRoot() {
        return (MutableTreeNode) root;
    }
}
