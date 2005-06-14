package org.mevenide.idea.repository.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.tree.TreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class LazyRepoTreeNode extends RepoTreeNode {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(LazyRepoTreeNode.class);

    /**
     * The status of this node.
     */
    private AtomicReference<NodeStatus> status = new AtomicReference<NodeStatus>(NodeStatus.INITIAL);

    public LazyRepoTreeNode(final RepositoryTreeModel pModel,
                            final TreeNode pParent,
                            final NodeDescriptor pNodeDescriptor,
                            final RepoPathElement... pPathElements) {
        super(pModel, pParent, pNodeDescriptor, pPathElements);
        final TreeNode msgNode = new MessageTreeNode(this);
        children.get().add(msgNode);
    }

    /**
     * Checks if this node has not fetched its children yet, and if not, starts a
     * seperate thread that will fetch this node's children and refresh the
     * owning model.
     */
    private boolean initiateFetchIfNecessary() {
        if (status.compareAndSet(NodeStatus.INITIAL, NodeStatus.RETRIEVING)) {
            final Runnable retriever = new ChildRetriever();
            final Thread retrieveThread = new Thread(retriever);
            retrieveThread.start();
            return true;
        }
        else
            return false;
    }

    @Override public TreeNode getChildAt(int childIndex) {
        initiateFetchIfNecessary();
        return super.getChildAt(childIndex);
    }

    @Override public int getChildCount() {
        initiateFetchIfNecessary();
        return super.getChildCount();
    }

    @Override public int getIndex(TreeNode node) {
        initiateFetchIfNecessary();
        return super.getIndex(node);
    }

    @Override public Enumeration<? extends TreeNode> children() {
        initiateFetchIfNecessary();
        return super.children();
    }

    /**
     * A runnable, intended to run in a different thread, that retrieves a node's children.
     */
    private class ChildRetriever implements Runnable {
        public void run() {

            //
            //retrieve all children into a map, keyed by a common node descriptor
            //and create a tree nodes list based on it
            //
            final List<TreeNode> nodes = createChildNodes(createNodeMap());

            //
            //sort the collection - this is an ugly hack, casting it to List
            //noinspection UNCHECKED_WARNING
            Collections.sort((List)nodes);

            //
            //save the new children list as this node's children list and mark
            //this node as ready
            //
            children.set(nodes);
            status.set(NodeStatus.READY);

            //
            //now that we've finished retrieving the children, we can notify
            //the tree model to refresh the tree
            //
            model.fireTreeStructureChanged(LazyRepoTreeNode.this);
        }

        /**
         * Creates and returns a list of tree nodes, created from the given
         * {@link NodesMap}. Each entry in the nodes map is translated into
         * a tree node, which represents that entry's repository path elements.
         *
         * @param pNodes the nodes map
         * @return list of tree nodes
         */
        private List<TreeNode> createChildNodes(final NodesMap pNodes) {
            final List<TreeNode> children = new ArrayList<TreeNode>(pNodes.size());
            for (Map.Entry<NodeDescriptor, List<RepoPathElement>> entry : pNodes.entrySet()) {
                final NodeDescriptor desc = entry.getKey();
                final List<RepoPathElement> nodeElts = entry.getValue();

                final RepoPathElement[] buffer = new RepoPathElement[nodeElts.size()];
                final RepoPathElement[] pathElements = nodeElts.toArray(buffer);
                final RepoTreeNode node = new LazyRepoTreeNode(model,
                                                               LazyRepoTreeNode.this,
                                                               desc,
                                                               pathElements);
                children.add(node);
            }

            return children;
        }

        /**
         * Retrieve all children into a map, keyed by a common node descriptor.
         *
         * <p>The returned map is keyed by a virtual node descriptor, which identifies a repository
         * node, regardless of the repository that it belongs to. This enables to aggregate nodes
         * from different repositories into a unified view.</p>
         *
         * <p>In each entry in the map (keyed by this virtual node descriptor) the value is a list
         * of {@link RepoPathElement}s the conform to the node descriptor, but come from different
         * repository readers.</p>
         *
         * <p>Each such entry in the map will later be translated to a single {@code RepoTreeNode}
         * that will represent all the {@code RepoPathElement}s for that entry.</p>
         *
         * @return a nodes map
         */
        private NodesMap createNodeMap() {
            //
            //retrieve all children into a map, keyed by a common node descriptor
            //this means that all children for a given "virtual" node (repository-
            //agnostic) are stored under a single key in the map. Each map
            //entry is then translated into a single tree node, which will
            //represent all the individual RepoPathElements for that virtual node.
            //
            final NodesMap fetched = new NodesMap(5);
            for (RepoPathElement element : pathElements) {
                final IRepositoryReader reader = element.getReader();
                if(!model.isShowLocal() && reader.getRootURI().getScheme().startsWith("file"))
                    continue;
                if(!model.isShowRemote() && reader.getRootURI().getScheme().startsWith("http"))
                    continue;

                final RepoPathElement[] childElts = fetchChildren(element);
                for (final RepoPathElement childElt : childElts) {

                    final NodeDescriptor desc = new NodeDescriptor(childElt.getLevel(),
                                                                   childElt.getGroupId(),
                                                                   childElt.getType(),
                                                                   childElt.getArtifactId(),
                                                                   childElt.getVersion(),
                                                                   childElt.getExtension());
                    if (!fetched.containsKey(desc)) {
                        final List<RepoPathElement> descItems = new ArrayList<RepoPathElement>(5);
                        descItems.add(childElt);
                        fetched.put(desc, descItems);
                    }
                    else
                        fetched.get(desc).add(childElt);
                }
            }

            return fetched;
        }

        /**
         * Fetches the children for the given {@link RepoPathElement}, ignoring (and logging) errors
         * if they occur. If errors do occur, an empty array is returned.
         *
         * @param pPathElement the parent to retrieve the children for
         * @return array of children (never {@code null}).
         */
        private RepoPathElement[] fetchChildren(final RepoPathElement pPathElement) {
            synchronized (pPathElement.getReader()) {
                try {
                    return pPathElement.getChildren();
                }
                catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    return new RepoPathElement[0];
                }
            }
        }
    }

    /**
     * A nodes map, keyed by a node descriptor, and contains a list of repository path elements for
     * each entry.
     */
    private static class NodesMap extends HashMap<NodeDescriptor, List<RepoPathElement>> {
        public NodesMap() {
        }

        public NodesMap(final int initialCapacity) {
            super(initialCapacity);
        }

        public NodesMap(final int initialCapacity, final float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public NodesMap(final Map<? extends NodeDescriptor, ? extends List<RepoPathElement>> m) {
            super(m);
        }
    }
}
