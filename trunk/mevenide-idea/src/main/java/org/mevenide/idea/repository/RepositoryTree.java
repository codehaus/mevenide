package org.mevenide.idea.repository;

import com.intellij.util.ui.Tree;
import com.intellij.openapi.project.Project;
import org.mevenide.repository.IRepositoryReader;

/**
 * @author Arik
 */
public class RepositoryTree extends Tree {

    public RepositoryTree(final Project pProject,
                          final IRepositoryReader pRepositoryReader) {
        super(new RepositoryTreeModel(pRepositoryReader));
        setShowsRootHandles(true);
        setRootVisible(false);
        setCellRenderer(new RepositoryTreeCellRenderer());
    }
}
