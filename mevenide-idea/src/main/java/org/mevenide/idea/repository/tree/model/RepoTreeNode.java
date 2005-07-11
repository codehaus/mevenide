package org.mevenide.idea.repository.tree.model;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.tree.TreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.ChildrenFetchService;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class RepoTreeNode implements TreeNode {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(RepoTreeNode.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepoTreeNode.class);

    /**
     * Thread factory used for launching threads that will update the node's children collection
     * when fetched.
     */
    private static final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();

    public static enum SearchStatus {
        NOT_SEARCHED,
        SEARCHING,
        SEARCHED
    }

    /**
     * The parent node.
     */
    private final TreeNode parent;

    /**
     * The repository path this node represents.
     */
    private final RepoPathElement pathElement;

    /**
     * Used for synchronizing access and retrieves.
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * The node to display when in the {@link SearchStatus#SEARCHING} mode.
     */
    private final MessageNode messageNode = new MessageNode(this, RES.get("wait.msg"));

    /**
     * The children list when in retrieve mode.
     */
    private final List<TreeNode> childrenWhenRetrieving = Collections.synchronizedList(
            new ArrayList<TreeNode>(1));

    /**
     * The list of retrieved node children.
     */
    private final List<TreeNode> children = Collections.synchronizedList(
            new ArrayList<TreeNode>(10));

    /**
     * Current node status.
     */
    private SearchStatus status = SearchStatus.NOT_SEARCHED;

    /**
     * Creates an instance with the given parent for the specified repository path.
     *
     * @param pParent the parent node
     * @param pPath   the repository path for this node
     */
    public RepoTreeNode(final TreeNode pParent,
                        final RepoPathElement pPath) {
        parent = pParent;
        pathElement = pPath;

        childrenWhenRetrieving.add(messageNode);
    }

    /**
     * Returns the repository path this node represents.
     *
     * @return repo node
     */
    public RepoPathElement getPathElement() {
        return pathElement;
    }

    public SearchStatus getStatus() {
        lock.lock();
        try {
            return status;
        }
        finally {
            lock.unlock();
        }
    }

    public TreeNode getChildAt(int childIndex) {
        lock.lock();
        try {
            switch (getStatus()) {
                case NOT_SEARCHED:
                    return null;
                case SEARCHING:
                    return childrenWhenRetrieving.get(childIndex);
                case SEARCHED:
                    return children.get(childIndex);
                default:
                    throw new IllegalStateException("Node is in an illegal state");
            }
        }
        finally {
            lock.unlock();
        }
    }

    public int getChildCount() {
        lock.lock();
        try {
            switch (getStatus()) {
                case NOT_SEARCHED:
                    return 0;
                case SEARCHING:
                    return childrenWhenRetrieving.size();
                case SEARCHED:
                    return children.size();
                default:
                    throw new IllegalStateException("Node is in an illegal state");
            }
        }
        finally {
            lock.unlock();
        }
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        lock.lock();
        try {
            switch (getStatus()) {
                case NOT_SEARCHED:
                    return -1;
                case SEARCHING:
                    return childrenWhenRetrieving.indexOf(node);
                case SEARCHED:
                    return children.indexOf(node);
                default:
                    throw new IllegalStateException("Node is in an illegal state");
            }
        }
        finally {
            lock.unlock();
        }
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;
    }

    public Enumeration children() {
        lock.lock();
        try {
            final List<TreeNode> listToClone;
            switch (getStatus()) {
                case NOT_SEARCHED:
                    return null;
                case SEARCHING:
                    listToClone = childrenWhenRetrieving;
                    break;
                case SEARCHED:
                    listToClone = children;
                    break;
                default:
                    throw new IllegalStateException("Node is in an illegal state");
            }

            final List<TreeNode> clone = new ArrayList<TreeNode>(listToClone);
            return Collections.enumeration(clone);
        }
        finally {
            lock.unlock();
        }
    }

    public void fetchChildren(final FetchStatusListener pListener) {
        lock.lock();
        try {
            if(status != SearchStatus.NOT_SEARCHED)
                return;

            THREAD_FACTORY.newThread(new ChildFetcher(pListener)).start();
        }
        finally {
            lock.unlock();
        }
    }

    private class ChildFetcher implements Runnable {
        private final FetchStatusListener listener;

        public ChildFetcher(final FetchStatusListener pListener) {
            listener = pListener;
        }

        private RepoPathElement[] getPathElements() throws InterruptedException, ExecutionException {
            final ChildrenFetchService executor = ChildrenFetchService.getInstance();
            final Future<RepoPathElement[]> fetchResult = executor.fetch(pathElement);
            try {
                status = SearchStatus.SEARCHING;
                listener.fetchStarted(RepoTreeNode.this);
                LOG.trace("Received fetch future for " + pathElement.getRootURI() + " - waiting for results...");
                final RepoPathElement[] children = fetchResult.get();
                LOG.trace("Received fetch future results for " + pathElement.getRootURI() + " - " + children.length);
                return children;
            }
            catch (InterruptedException e) {
                throw e;
            }
            catch (ExecutionException e) {
                throw e;
            }
        }

        public void run() {
            RepoPathElement[] nodes;
            try {
                nodes = getPathElements();
            }
            catch(Exception e) {
                LOG.trace(e.getMessage(), e);
                final RepoTreeNode parent = RepoTreeNode.this;
                if (e instanceof CancellationException)
                    listener.fetchCancelled(parent, (CancellationException) e);
                else if (e instanceof InterruptedException)
                    listener.fetchInterrupted(parent, (InterruptedException) e);
                else
                    listener.fetchError(parent, e);
                return;
            }

            lock.lock();
            try {
                LOG.trace("Received " + nodes.length + " results for " + pathElement.getRootURI() + " - creating tree nodes");
                final SortedSet<RepoTreeNode> treeNodes;
                treeNodes = new TreeSet<RepoTreeNode>(new NodeComparator());

                for (RepoPathElement node : nodes)
                    treeNodes.add(new RepoTreeNode(RepoTreeNode.this, node));

                children.clear();
                children.addAll(treeNodes);

                //
                //notify tree that fetch is successful
                //
                status = SearchStatus.SEARCHED;
                listener.fetchComplete(RepoTreeNode.this);
            }
            catch (Exception e) {
                LOG.error(e.getMessage(), e);
                status = SearchStatus.SEARCHED;
                listener.fetchError(RepoTreeNode.this, e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    private class NodeComparator implements Comparator<RepoTreeNode> {
        public int compare(final RepoTreeNode o1, final RepoTreeNode o2) {
            final RepoPathElement e1 = o1.getPathElement();
            final RepoPathElement e2 = o2.getPathElement();

            return e1.getRelativeURIPath().compareTo(e2.getRelativeURIPath());
        }
    }
}
