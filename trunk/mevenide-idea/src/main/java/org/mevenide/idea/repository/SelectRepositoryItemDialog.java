package org.mevenide.idea.repository;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
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

    public IRepositoryReader getRepositoryReader() {
        return repositoryReader;
    }

    public void setRepositoryReader(final IRepositoryReader pRepositoryReader) {
        repositoryReader = pRepositoryReader;
    }

    public RepoPathElement[] show(final Project pProject, final String pTitle) {
        if(repositoryReader == null)
            throw new IllegalStateException(RES.get("repo.reader.missing"));

        final RepositoryTree tree = new RepositoryTree(pProject, repositoryReader);
        final JScrollPane scrollPane = new JScrollPane(tree);

        final DialogBuilder builder = new DialogBuilder(pProject);
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(scrollPane);
        builder.setTitle(pTitle);

        final int exitCode = builder.show();
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {
            final TreePath[] selectedPaths = tree.getSelectionPaths();
            final List<RepoPathElement> items = new ArrayList<RepoPathElement>(selectedPaths.length);
            for(TreePath path : selectedPaths) {
                final Object node = path.getLastPathComponent();
                if(!(node instanceof RepoPathElement))
                    continue;

                final RepoPathElement elt = (RepoPathElement) node;
                final int level = elt.getLevel();
                switch(level) {
                    case RepoPathElement.LEVEL_GROUP:
                        if (allowingGroups)
                            items.add(elt);
                        break;
                    case RepoPathElement.LEVEL_ROOT:
                        if (allowingRoot)
                            items.add(elt);
                        break;
                    case RepoPathElement.LEVEL_TYPE:
                        if (allowingTypes)
                            items.add(elt);
                        break;
                    case RepoPathElement.LEVEL_VERSION:
                        if (allowingArtifacts)
                            items.add(elt);
                        break;
                }
            }

            return items.toArray(new RepoPathElement[items.size()]);
        }
        else
            return null;
    }
}
