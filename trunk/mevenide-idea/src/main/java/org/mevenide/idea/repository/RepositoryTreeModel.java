package org.mevenide.idea.repository;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.mevenide.idea.Res;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.apache.commons.lang.ArrayUtils;

/**
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
     * The root node (merely a string).
     */
    private final RepoPathElement root;

    /**
     * Creates an instance for the given repository reader.
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
        if(pNodeObject instanceof RepoPathElement)
            return (RepoPathElement) pNodeObject;
        else
            throw new IllegalArgumentException(RES.get("wrong.arg.type",
                                                       "pNodeObject",
                                                       RepoPathElement.class.getName()));
    }

    protected final RepoPathElement[] getRepoNodeChildren(final Object pNodeObject) {
        try {
            return getRepoNode(pNodeObject).getChildren();
        }
        catch (Exception e) {
            throw new RepositoryReadException(e);
        }
    }
}
