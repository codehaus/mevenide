package org.mevenide.idea.repository.tree.model;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * @author Arik
 */
public class MessageNode implements TreeNode {
    private final TreeNode parent;
    private final String message;

    public MessageNode(final TreeNode pParent, final String pMessage) {
        parent = pParent;
        message = pMessage;
    }

    public String getMessage() {
        return message;
    }

    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    public int getChildCount() {
        return 0;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return -1;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public Enumeration children() {
        return null;
    }
}
