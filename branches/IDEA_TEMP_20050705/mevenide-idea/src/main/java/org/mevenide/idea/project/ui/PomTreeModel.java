package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.PomManagerEvent;
import org.mevenide.idea.project.PomManagerListener;
import java.util.Enumeration;

/**
 * @author Arik
 */
public class PomTreeModel extends DefaultTreeModel implements Disposable, PomManagerListener {

    private final Project project;

    public PomTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode(), true);

        project = pProject;
        final PomManager pomManager = PomManager.getInstance(project);
        pomManager.addPomManagerListener(this);

        final MutableTreeNode root = getRoot();
        final VirtualFilePointer[] pointers = pomManager.getPomPointers();
        for (VirtualFilePointer pointer : pointers) {
            final PomNode node = createPomNode(pointer);
            root.insert(node, root.getChildCount());
        }
    }

    public MutableTreeNode getRoot() {
        return (MutableTreeNode) super.getRoot();
    }

    public void dispose() {
        final PomManager pomManager = PomManager.getInstance(project);
        pomManager.removePomManagerListener(this);
    }

    public void pomAdded(final PomManagerEvent pEvent) {
        final MutableTreeNode root = getRoot();
        final VirtualFilePointer pointer = pEvent.getFilePointer();

        final PomNode node = createPomNode(pointer);
        root.insert(node, root.getChildCount());
        nodesWereInserted(root, new int[]{root.getIndex(node)});
    }

    public void pomRemoved(final PomManagerEvent pEvent) {
        final DefaultMutableTreeNode node = findPomNode(pEvent.getFilePointer());
        if(node != null) {
            final MutableTreeNode root = getRoot();
            final int index = root.getIndex(node);
            node.removeFromParent();
            nodesWereRemoved(root, new int[]{index}, new Object[]{node});
        }
    }

    public void pomValidityChanged(final PomManagerEvent pEvent) {
        final PomNode node = findPomNode(pEvent.getFilePointer());
        if (node != null) {
            node.removeAllChildren();
            nodeStructureChanged(node);
        }
    }

    private PomNode createPomNode(final VirtualFilePointer pPointer) {
        return new PomNode(pPointer);
    }

    private PomNode findPomNode(final VirtualFilePointer pPointer) {
        final MutableTreeNode root = getRoot();

        //noinspection UNCHECKED_WARNING
        final Enumeration<PomNode> children = root.children();
        while (children.hasMoreElements()) {
            final PomNode node = children.nextElement();
            if (pPointer.equals(node.getUserObject()))
                return node;
        }

        return null;
    }
}
