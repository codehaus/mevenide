package org.mevenide.idea.repository.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.tree.TreeNode;

import org.mevenide.idea.Res;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public abstract class RepoTreeNode implements TreeNode, Comparable {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepoTreeNode.class);

    /**
     * The tree model this node belongs to. We need a reference to it, to notify
     * it when we finish retrieving children.
     */
    protected final RepositoryTreeModel model;

    /**
     * The node descriptor - specifies the location of this node, in a
     * repository-agnostic way, in the virtual repository.
     */
    protected final NodeDescriptor nodeDescriptor;

    /**
     * The repository path elements this node represents. Since we may be
     * combining several {@link org.mevenide.repository.IRepositoryReader}s,
     * we must be able to have several path elements that are at the same
     * level, but belong to different repositories.
     */
    protected final RepoPathElement[] pathElements;

    /**
     * This node's parent.
     */
    protected final TreeNode parent;

    /**
     * The children this node has.
     */
    protected final AtomicReference<List<TreeNode>> children;

    /**
     * Creates an instance for the given model and path elements.
     *
     * @param pModel the model this node belongs to
     * @param pPathElements path elements this node represents
     */
    public RepoTreeNode(final RepositoryTreeModel pModel,
                        final TreeNode pParent,
                        final NodeDescriptor pNodeDescriptor,
                        final RepoPathElement... pPathElements) {
        if(pModel == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pModel"));

        model = pModel;
        nodeDescriptor = pNodeDescriptor;
        pathElements = pPathElements;
        parent = pParent;

        final List<TreeNode> childList = new ArrayList<TreeNode>(5);
        children = new AtomicReference<List<TreeNode>>(childList);
    }

    public boolean isLeaf() {
        for (RepoPathElement element : pathElements) {
            if(!element.isLeaf())
                return false;
        }

        return true;
    }

    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children.get());
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public TreeNode getChildAt(int childIndex) {
        return children.get().get(childIndex);
    }

    public int getChildCount() {
        return children.get().size();
    }

    public int getIndex(TreeNode node) {
        return children.get().indexOf(node);
    }

    public TreeNode getParent() {
        return parent;
    }

    /**
     * Returns this node's node descriptor.
     *
     * <p>The node descriptor specifies the location of this node, in a
     * repository-agnostic way, in the virtual repository.</p>
     *
     * @return node descriptor.
     */
    public final NodeDescriptor getNodeDescriptor() {
        return nodeDescriptor;
    }

    /**
     * Returns the path elements this node represents.
     *
     * @return repository path elements
     */
    public final RepoPathElement[] getPathElements() {
        return pathElements;
    }

    /**
     * Returns the path element for the given repository reader. Each repository tree node might
     * represent different repository path elements from different repository readers - this method
     * can return the path element for a specific repository reader.
     *
     * @param pRepo the repository reader for which to search for a path element
     * @return a path element, or {@code null} if no path element for the given repository is found
     *         in this node
     */
    public final RepoPathElement getPathElementForRepository(final IRepositoryReader pRepo) {
        for (RepoPathElement pathElement : pathElements)
            if (pathElement != null && pathElement.getReader().equals(pRepo))
                return pathElement;

        return null;
    }

    public int compareTo(final Object o) {
        final RepoTreeNode that = (RepoTreeNode) o;
        return nodeDescriptor.compareTo(that.nodeDescriptor);
    }
}
