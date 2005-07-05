package org.mevenide.idea.project.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;

/**
 * @author Arik
 */
public class PomNode extends DefaultMutableTreeNode {

    public PomNode(final VirtualFilePointer pPointer) {
        super(pPointer, true);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public VirtualFilePointer getUserObject() {
        return (VirtualFilePointer) super.getUserObject();
    }
}
