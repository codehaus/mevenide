package org.mevenide.idea.repository.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.mevenide.idea.repository.tree.RepoTree;
import org.mevenide.idea.repository.browser.RepositoryBrowser;
import org.mevenide.idea.repository.tree.model.RepoTreeNode;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class SelectRepositoryItemDialog {
    private boolean allowingRoot = false;
    private boolean allowingGroups = false;
    private boolean allowingTypes = false;
    private boolean allowingArtifacts = false;
    private boolean allowingVersions = false;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String pTitle) {
        title = pTitle;
    }

    public boolean isAllowingArtifacts() {
        return allowingArtifacts;
    }

    public void setAllowingArtifacts(final boolean pAllowingArtifacts) {
        allowingArtifacts = pAllowingArtifacts;
    }

    public boolean isAllowingGroups() {
        return allowingGroups;
    }

    public void setAllowingGroups(final boolean pAllowingGroups) {
        allowingGroups = pAllowingGroups;
    }

    public boolean isAllowingRoot() {
        return allowingRoot;
    }

    public void setAllowingRoot(final boolean pAllowingRoot) {
        allowingRoot = pAllowingRoot;
    }

    public boolean isAllowingTypes() {
        return allowingTypes;
    }

    public void setAllowingTypes(final boolean pAllowingTypes) {
        allowingTypes = pAllowingTypes;
    }

    public boolean isAllowingVersions() {
        return allowingVersions;
    }

    public void setAllowingVersions(final boolean pAllowingVersions) {
        allowingVersions = pAllowingVersions;
    }

    public RepoPathElement[] show(final Project pProject) {
        final DialogBuilder builder = new DialogBuilder(pProject);
        final RepositoryBrowser browser = new DialogRepoBrowser(pProject, builder);

        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(browser);
        builder.setTitle(title == null || title.trim().length() == 0 ? "Browse Repository" : title);
        builder.setOkActionEnabled(false);

        final int exitCode = builder.show();
        if (exitCode == DialogWrapper.OK_EXIT_CODE)
            return browser.getSelectedItems();
        else
            return null;
    }

    private class MyTreeSelectionListener implements TreeSelectionListener {
        private final DialogBuilder builder;

        public MyTreeSelectionListener(final DialogBuilder pBuilder) {
            builder = pBuilder;
        }

        public void valueChanged(TreeSelectionEvent e) {
            final JTree tree = (JTree) e.getSource();
            builder.setOkActionEnabled(shouldEnableOk(tree.getSelectionPaths()));
        }

        private boolean shouldEnableOk(final TreePath[] pTreePaths) {
            if (pTreePaths == null)
                return false;

            for (TreePath path : pTreePaths) {
                final Object value = path.getLastPathComponent();
                if (!(value instanceof RepoTreeNode))
                    return false;

                final RepoTreeNode node = (RepoTreeNode) value;
                final RepoPathElement desc = node.getPathElement();
                switch (desc.getLevel()) {
                    case RepoPathElement.LEVEL_GROUP:
                        if (!allowingGroups)
                            return false;
                        break;
                    case RepoPathElement.LEVEL_ROOT:
                        if (!allowingRoot)
                            return false;
                        break;
                    case RepoPathElement.LEVEL_TYPE:
                        if (!allowingTypes)
                            return false;
                        break;
                    case RepoPathElement.LEVEL_ARTIFACT:
                        if (!allowingArtifacts)
                            return false;
                        break;
                    case RepoPathElement.LEVEL_VERSION:
                        if (!allowingVersions)
                            return false;
                        break;
                }
            }

            return true;
        }
    }

    private class DialogRepoBrowser extends RepositoryBrowser {
        private final SelectRepositoryItemDialog.MyTreeSelectionListener selectionListener;

        public DialogRepoBrowser(final Project pProject,
                                 final DialogBuilder pBuilder) {
            super(pProject);
            selectionListener = new MyTreeSelectionListener(pBuilder);
        }

        @Override
        protected RepoTree addRepo(final IRepositoryReader pRepo) {
            final RepoTree tree = super.addRepo(pRepo);
            tree.addTreeSelectionListener(selectionListener);
            return tree;
        }
    }
}
