package org.mevenide.idea.repository.tree;

import com.intellij.util.ui.Tree;
import java.util.concurrent.CancellationException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import org.mevenide.idea.repository.tree.model.FetchStatusListener;
import org.mevenide.idea.repository.tree.model.RepoTreeNode;
import org.mevenide.idea.repository.tree.model.RepoTreeModel;

/**
 * @author Arik
 */
public class RepoTree extends Tree {
    private final NodeExpander nodeExpander = new NodeExpander();

    public RepoTree() {
        this(createDummyModel());
    }

    public RepoTree(final TreeModel pModel) {
        super(pModel);
        addTreeWillExpandListener(nodeExpander);
        init();
    }

    public static DummyModel createDummyModel() {
        return new DummyModel();
    }

    @Override
    public void setModel(final TreeModel newModel) {
        if (newModel instanceof RepoTreeModel || newModel instanceof DummyModel) {
            super.setModel(newModel);
            init();
        }
        else
            throw new IllegalArgumentException("Illegal model - " + newModel);
    }

    private void init() {
        if (!getShowsRootHandles())
            setShowsRootHandles(true);
        if (!isRootVisible())
            setRootVisible(true);
        if (!(getCellRenderer() instanceof RepoTreeCellRenderer))
            setCellRenderer(new RepoTreeCellRenderer());
    }

    public void fetchNode(final TreeNode pNode) {
        if(pNode instanceof RepoTreeNode) {
            final RepoTreeNode node = (RepoTreeNode) pNode;
            node.fetchChildren(nodeExpander);
        }
    }

    private class NodeExpander implements TreeWillExpandListener, FetchStatusListener {
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            synchronized(RepoTree.this) {
                if (event.getSource() != RepoTree.this)
                    return;

                final TreePath path = event.getPath();
                final Object last = path.getLastPathComponent();
                if (!(last instanceof RepoTreeNode))
                    return;

                final RepoTreeNode node = (RepoTreeNode) last;
                node.fetchChildren(this);
            }
        }

        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }

        private void notifyChange(final RepoTreeNode pNode) {
//            synchronized (RepoTree.this) {
                final TreeModel model = getModel();
                if (!(model instanceof RepoTreeModel))
                    return;

                //
                //make sure the node that fired this change still belongs
                //to our model (if the model was swapped in the middle of
                //a fetch operation, this can happen)
                //
                TreeNode parent = pNode;
                while(parent.getParent() != null)
                    parent = parent.getParent();
                if(!parent.equals(model.getRoot()))
                    return;

                //
                //notify that the structure of the given node has been changed
                //
                final RepoTreeModel repoModel = (RepoTreeModel) model;
                repoModel.nodeStructureChanged(pNode);
//            }
        }

        public void fetchStarted(final RepoTreeNode pNode) {
            notifyChange(pNode);
        }

        public void fetchComplete(final RepoTreeNode pNode) {
            notifyChange(pNode);
        }

        public void fetchCancelled(final RepoTreeNode pNode,
                                   final CancellationException pCause) {
            notifyChange(pNode);
        }

        public void fetchError(final RepoTreeNode pNode, final Exception pCause) {
            notifyChange(pNode);
        }

        public void fetchInterrupted(final RepoTreeNode pNode,
                                     final InterruptedException pCause) {
            notifyChange(pNode);
        }
    }

    private static class DummyModel implements TreeModel {
        public Object getRoot() {
            return null;
        }

        public Object getChild(Object parent, int index) {
            return null;
        }

        public int getChildCount(Object parent) {
            return 0;
        }

        public boolean isLeaf(Object node) {
            return true;
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        public int getIndexOfChild(Object parent, Object child) {
            return -1;
        }

        public void addTreeModelListener(TreeModelListener l) {
        }

        public void removeTreeModelListener(TreeModelListener l) {
        }
    }
}
