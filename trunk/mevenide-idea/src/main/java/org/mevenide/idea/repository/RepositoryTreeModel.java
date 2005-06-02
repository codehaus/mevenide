package org.mevenide.idea.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * <p>Code for 'fireXXX' methods copied from javax.swing.tree.DefaultTreeModel since there is no
 * javax.swing.tree.AbstractTreeModel we can extend, and we shouldn't extend DefaultTreeModel.</p>
 *
 * @author Arik
 */
public class RepositoryTreeModel implements TreeModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(RepositoryTreeModel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryTreeModel.class);

    /**
     * Used to synchronize node-retrieval requests (as the repository readers are
     * not thread-safe, we cannot retrieve multiple nodes at the same time).
     */
    private final Object LOCK = new Object();

    /**
     * An empty array of repository path elements. Recycled to save redundant
     * instances.
     */
    private static final RepoPathElement[] EMPTY_REPO_ELEMENTS_ARRAY = new RepoPathElement[0];

    /**
     * Manages model listeners.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The root node (merely a string).
     */
    private final RepoPathElement root;

    /**
     * Every node who's children have been successfuly retrieved is added to this
     * set, to mark that no further threads need to be created to retrieve its
     * content.
     *
     * <p>If a node does not appear here, than it has not been queried and
     * a seperate thread will be created that will process its query and later
     * update the model.</p>
     */
    private final Set<RepoPathElement> queriedNodes = Collections.synchronizedSet(new HashSet<RepoPathElement>(10));

    /**
     * Every node that is being queried in a seperate thread, is added to this set. This is used to
     * prevent two seperate threads querying the same node.
     */
    private final Set<RepoPathElement> retrievingNodes = Collections.synchronizedSet(new HashSet<RepoPathElement>(10));

    /**
     * Creates an instance for the given repository reader.
     *
     * @param pRepositoryReader the repository reader
     */
    public RepositoryTreeModel(final IRepositoryReader pRepositoryReader) {
        root = new RepoPathElement(pRepositoryReader);
    }

    public Object getChild(Object parent, int index) {
        return getRepoNodeChildren(parent)[index];
    }

    public int getChildCount(Object parent) {
        return getRepoNodeChildren(parent).length;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ArrayUtils.indexOf(getRepoNodeChildren(parent), child);
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return getRepoNode(node).isLeaf();
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

    protected final RepoPathElement getRepoNode(final Object pNodeObject) {
        if (pNodeObject instanceof RepoPathElement)
            return (RepoPathElement) pNodeObject;
        else
            throw new IllegalArgumentException(RES.get("wrong.arg.type",
                                                       "pNodeObject",
                                                       RepoPathElement.class.getName()));
    }

    protected final RepoPathElement[] getRepoNodeChildren(final Object pNodeObject) {
        final RepoPathElement parent = getRepoNode(pNodeObject);

        if (queriedNodes.contains(parent))
            return retrieveNodeChildren(parent);

        if(!retrievingNodes.contains(parent)) {
            final Runnable retriever = new Runnable() {
                public void run() {

                    //
                    //start retrieving the node's children
                    //
                    retrievingNodes.add(parent);
                    try {
                        synchronized (LOCK) {
                            LOG.trace("Retrieving children for node " + parent.getURI());
                            retrieveNodeChildren(pNodeObject);
                        }
                    }
                    catch (RepositoryReadException e) {
                        LOG.error(e.getMessage(), e);
                        retrievingNodes.remove(parent);
                        return;
                    }

                    //
                    //mark the node as retrieved (and not being retrieved anymore)
                    //
                    queriedNodes.add(parent);
                    retrievingNodes.remove(parent);

                    //
                    //notify the listeners (and the tree) that the node has changed
                    //
                    fireTreeStructureChanged(parent, new TreePath(getPathToRoot(parent)));
                }
            };

            final Thread retrieverThread = new Thread(retriever, "RepositoryTreeRetriever");
            retrieverThread.start();
        }

        return EMPTY_REPO_ELEMENTS_ARRAY;
    }

    private RepoPathElement[] retrieveNodeChildren(final Object pNodeObject)
            throws RepositoryReadException {
        try {
            return getRepoNode(pNodeObject).getChildren();
        }
        catch (Exception e) {
            throw new RepositoryReadException(e);
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The
     * event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source       the node being changed
     * @param path         the path to the root node
     * @param childIndices the indices of the changed elements
     * @param children     the changed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
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
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The
     * event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source       the node where new elements are being inserted
     * @param path         the path to the root node
     * @param childIndices the indices of the new elements
     * @param children     the new elements
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
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
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The
     * event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source       the node where elements are being removed
     * @param path         the path to the root node
     * @param childIndices the indices of the removed elements
     * @param children     the removed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
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
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
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

    /*
    * Notifies all listeners that have registered interest for
    * notification on this event type.  The event instance
    * is lazily created using the parameters passed into
    * the fire method.
    *
    * @param source the node where the tree model has changed
    * @param path the path to the root node
    * @see EventListenerList
    */
    protected void fireTreeStructureChanged(Object source, TreePath path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Builds the parents of node up to and including the root node, where the original node is the
     * last element in the returned array. The length of the returned array gives the node's depth
     * in the tree.
     *
     * @param aNode the node to get the path for
     */
    public RepoPathElement[] getPathToRoot(RepoPathElement aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node, where the original node is the
     * last element in the returned array. The length of the returned array gives the node's depth
     * in the tree.
     *
     * @param aNode the node to get the path for
     * @param depth an int giving the number of steps already taken towards the root (on recursive
     *              calls), used to size the returned array
     * @return an array of {@code RepoPathElement} giving the path from the root to the specified
     *         node
     */
    protected RepoPathElement[] getPathToRoot(RepoPathElement aNode, int depth) {
        RepoPathElement[] retNodes;

        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        // Check for null, in case someone passed in a null node, or
        // they passed in an element that isn't rooted at root.
        if (aNode == null) {
            if (depth == 0)
                return null;
            else
                retNodes = new RepoPathElement[depth];
        }
        else {
            depth++;
            if (aNode == root)
                retNodes = new RepoPathElement[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

}
