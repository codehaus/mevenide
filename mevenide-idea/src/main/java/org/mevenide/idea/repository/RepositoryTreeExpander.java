package org.mevenide.idea.repository;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.intellij.ide.TreeExpander;

/**
 * @author Arik
 */
public class RepositoryTreeExpander implements TreeExpander {

    private final JTree tree;

    public RepositoryTreeExpander(final JTree pTree) {
        tree = pTree;
    }

    public boolean canCollapse() {
        final TreeModel model = tree.getModel();

        final Object root = model.getRoot();
        if (tree.isCollapsed(new TreePath(root)))
            return false;

        final Object[] treePath = new Object[2];
        treePath[0] = root;

        final int childCount = model.getChildCount(root);
        for(int i = 0; i < childCount; i++) {
            treePath[1] = model.getChild(root, i);
            if(tree.isExpanded(new TreePath(treePath)))
                return true;
        }

        return false;
    }

    public boolean canExpand() {
        return false;
    }

    public void collapseAll() {
        final TreeModel model = tree.getModel();

        final Object root = model.getRoot();
        final Object[] treePath = new Object[2];
        treePath[0] = root;

        final int childCount = model.getChildCount(root);
        for (int i = 0; i < childCount; i++) {
            treePath[1] = model.getChild(root, i);
            tree.collapsePath(new TreePath(treePath));
        }
    }

    public void expandAll() {
    }
}
