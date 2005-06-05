package org.mevenide.idea.repository.model;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mevenide.idea.Res;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * A tree model for repository browsing trees.
 *
 * <p>This model uses the actual {@link RepoPathElement} objects as the actual tree nodes, rather
 * than the {@link javax.swing.tree.TreeNode} interface, as there is no need to create additional
 * object per node.</p>
 *
 * <p>Code for 'fireXXX' and 'getPathToRoot(*)' methods copied from {@link
 * javax.swing.tree.DefaultTreeModel} since there is no javax.swing.tree.AbstractTreeModel we can
 * extend, and we shouldn't extend {@code DefaultTreeModel}.</p>
 *
 * @author Arik
 */
public class RepositoryTreeModel implements TreeModel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryTreeModel.class);

    /**
     * Manages model listeners.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The root node.
     */
    private final TreeNode root;

    private boolean showLocal = true;
    private boolean showRemote = true;

    /**
     * Creates an instance for the given repository reader.
     *
     * @param pRepositoryReader the repository reader
     */
    public RepositoryTreeModel(final IRepositoryReader... pRepositoryReader) {
        final RepoPathElement[] elements = new RepoPathElement[pRepositoryReader.length];
        for (int i = 0; i < pRepositoryReader.length; i++) {
            IRepositoryReader reader = pRepositoryReader[i];
            elements[i] = new RepoPathElement(reader);
        }

        root = new LazyRepoTreeNode(this, null, new NodeDescriptor(), elements);
    }

    public Object getChild(Object parent, int index) {
        return ((TreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((TreeNode) parent).getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeNode) parent).getIndex((TreeNode) child);
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return ((TreeNode) node).isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException(RES.get("unsupp.op",
                                                        "valueForPathChanged",
                                                        RepositoryTreeModel.class.getName()));
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public boolean isShowLocal() {
        return showLocal;
    }

    public void setShowLocal(final boolean pShowLocal) {
        showLocal = pShowLocal;
    }

    public boolean isShowRemote() {
        return showRemote;
    }

    public void setShowRemote(final boolean pShowRemote) {
        showRemote = pShowRemote;
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The
     * event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source       the node where the tree model has changed
     * @param path         the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children     the affected elements
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Notifies all interested listeners that the entire structure inside the
     * given node has changed.
     *
     * @param pNode the node where the tree model has changed
     * @param pNodePath   the path to the root node
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(final TreeNode pNode,
                                            final TreePath pNodePath) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;

        //
        //Process the listeners last to first, notifying
        //those that are interested in this event
        //
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {

                if (e == null)
                    e = new TreeModelEvent(pNode, pNodePath);

                final TreeModelListener listener = (TreeModelListener) listeners[i + 1];
                listener.treeStructureChanged(e);
            }
        }
    }

    /**
     * Notifies the model listeners the entire structure inside the given node has changed.
     *
     * @param pNode the node whose structure has changed
     */
    public void fireTreeStructureChanged(final TreeNode pNode) {
        fireTreeStructureChanged(pNode, new TreePath(getPathToRoot(pNode)));
    }

    /**
     * Builds the parents of node up to and including the root node, where the original node is the
     * last element in the returned array. The length of the returned array gives the node's depth
     * in the tree.
     *
     * @param aNode the node to get the path for
     */
    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node, where the original node is the
     * last element in the returned array. The length of the returned array gives the node's depth
     * in the tree.
     *
     * @param pNode the node to get the path for
     * @param pDepth an int giving the number of steps already taken towards the root (on recursive
     *              calls), used to size the returned array
     * @return an array of {@code RepoPathElement} giving the path from the root to the specified
     *         node
     */
    protected TreeNode[] getPathToRoot(final TreeNode pNode, int pDepth) {
        TreeNode[] retNodes;

        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        // Check for null, in case someone passed in a null node, or
        // they passed in an element that isn't rooted at root.
        if (pNode == null) {
            if (pDepth == 0)
                return null;
            else
                retNodes = new TreeNode[pDepth];
        }
        else {
            pDepth++;
            if (pNode == root)
                retNodes = new TreeNode[pDepth];
            else
                retNodes = getPathToRoot(pNode.getParent(), pDepth);
            retNodes[retNodes.length - pDepth] = pNode;
        }
        return retNodes;
    }
}
