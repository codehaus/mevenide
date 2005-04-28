package org.mevenide.idea.util.ui.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author Arik
 */
public abstract class AbstractTreeModel extends DefaultTreeModel {

    protected AbstractTreeModel() {
        super(null);

    }

    protected AbstractTreeModel(final TreeNode pRoot) {
        super(pRoot);
    }

    protected AbstractTreeModel(final TreeNode pRoot,
                                final boolean pAsksAllowsChildren) {
        super(pRoot, pAsksAllowsChildren);
    }

    public MutableTreeNode getMutableRoot() {
        return (MutableTreeNode) root;
    }

    protected static TreeNode findNode(final TreeNode pNode,
                                       final NodeVisitor pVisitor) {
        return findNode(pNode, pVisitor, Integer.MAX_VALUE);
    }

    protected static TreeNode findNode(final TreeNode pNode,
                                       final NodeVisitor pVisitor,
                                       final int pLevel) {
        return findNode(pNode, pVisitor, pLevel, 0);
    }

    private static TreeNode findNode(final TreeNode pNode,
                                     final NodeVisitor pVisitor,
                                     final int pLevel,
                                     final int pCurrentLevel) {
        if(pVisitor.accept((pNode)))
            return pNode;

        final int childCount = pNode.getChildCount();
        for(int i = 0; i < childCount; i++) {
            final TreeNode node = pNode.getChildAt(i);
            if(pLevel == pCurrentLevel) {
                if(pVisitor.accept(node))
                    return node;
            }
            else {
                final TreeNode acceptedNode = findNode(node,
                                                       pVisitor,
                                                       pLevel,
                                                       pCurrentLevel + 1);
                if(acceptedNode != null)
                    return acceptedNode;
            }
        }

        return null;
    }

    protected static interface NodeVisitor {
        boolean accept(TreeNode pNode);
    }
}
