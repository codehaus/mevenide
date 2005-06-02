package org.mevenide.idea.repository;

import javax.swing.tree.TreeModel;

import com.intellij.util.ui.Tree;

/**
 * @author Arik
 */
public class RepositoryTree extends Tree {

    public RepositoryTree() {

    }

    public RepositoryTree(final TreeModel pModel) {
        super(pModel);
        setShowsRootHandles(true);
        setRootVisible(false);
        setCellRenderer(new RepositoryTreeCellRenderer());
    }
}
