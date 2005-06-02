package org.mevenide.idea.repository;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import org.mevenide.idea.Res;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class SelectRepositoryItemDialog {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SelectRepositoryItemDialog.class);

    private IRepositoryReader repositoryReader;
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

    public IRepositoryReader getRepositoryReader() {
        return repositoryReader;
    }

    public void setRepositoryReader(final IRepositoryReader pRepositoryReader) {
        repositoryReader = pRepositoryReader;
    }

    public RepoPathElement[] show(final Project pProject) {
        if (repositoryReader == null)
            throw new IllegalStateException(RES.get("repo.reader.missing"));

        final RepositoryTree tree = new RepositoryTree(new RepositoryTreeModel(repositoryReader));
        final JScrollPane scrollPane = new JScrollPane(tree);

        final DialogBuilder builder = new DialogBuilder(pProject);
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(scrollPane);
        builder.setTitle(title == null || title.trim().length() == 0 ? "Browse Repository" : title);
        builder.setOkActionEnabled(false);

        tree.addTreeSelectionListener(new MyTreeSelectionListener(builder));


        final int exitCode = builder.show();
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {

            final TreePath[] selectedPaths = tree.getSelectionPaths();
            final List<RepoPathElement> items = new ArrayList<RepoPathElement>(selectedPaths.length);
            for (TreePath path : selectedPaths) {
                final Object node = path.getLastPathComponent();
                if (node instanceof RepoPathElement)
                    items.add((RepoPathElement) node);
            }

            return items.toArray(new RepoPathElement[items.size()]);
        }
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
                if (!(value instanceof RepoPathElement))
                    return false;

                final RepoPathElement node = (RepoPathElement) value;
                switch (node.getLevel()) {
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
}
