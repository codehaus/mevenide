package org.mevenide.idea.repository;

import com.intellij.util.ui.Tree;
import javax.swing.tree.TreeModel;

/**
 * @author Arik
 */
public class RepositoryTree extends Tree {
    public RepositoryTree() {
        setShowsRootHandles(true);
        setRootVisible(false);
        setCellRenderer(new RepositoryTreeCellRenderer());
    }

    public RepositoryTree(final TreeModel pModel) {
        super(pModel);
    }

    @Override
    public final void setModel(TreeModel newModel) {
        super.setModel(newModel);
        setShowsRootHandles(true);
        setRootVisible(false);
        setCellRenderer(new RepositoryTreeCellRenderer());
    }
}
