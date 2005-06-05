package org.mevenide.idea.repository.model;

import java.util.Enumeration;
import java.util.Collections;
import javax.swing.tree.TreeNode;

import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class MessageTreeNode implements TreeNode {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(MessageTreeNode.class);

    private final TreeNode parent;
    private String message;

    public MessageTreeNode(final TreeNode pParent) {
        this(pParent, RES.get("wait.msg"));
    }

    public MessageTreeNode(final TreeNode pParent,
                           final String pMessage) {
        parent = pParent;
        message = pMessage;
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unchecked")
    public Enumeration children() {
        return Collections.enumeration(Collections.EMPTY_LIST);
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    public int getChildCount() {
        return 0;
    }

    public int getIndex(TreeNode node) {
        return -1;
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return true;
    }
}
