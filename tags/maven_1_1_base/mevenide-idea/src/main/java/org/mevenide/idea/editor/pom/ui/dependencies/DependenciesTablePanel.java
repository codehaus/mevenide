package org.mevenide.idea.editor.pom.ui.dependencies;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.repository.util.SelectRepositoryItemDialog;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class DependenciesTablePanel extends CRUDTablePanel<DependenciesTableModel> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependenciesTablePanel.class);

    protected static final String BROWSE_REPO_DLG_TITLE = "Select artifact";

    private final JButton browseDependencyButton = new JButton(RES.get("browse.dep.label"));
    private final PsiDependencies dependencies;

    public DependenciesTablePanel(final PsiDependencies pModel) {
        super(pModel.getXmlFile(), new DependenciesTableModel(pModel));

        dependencies = pModel;
        component.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        browseDependencyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final DependencyBrowseDialog dlg = new DependencyBrowseDialog();
                final RepoPathElement[] selectedElements = dlg.show(project);
                if (selectedElements == null || selectedElements.length == 0)
                    return;

                for (RepoPathElement path : selectedElements) {
                    final int row = dependencies.appendRow();
                    dependencies.setGroupId(row, path.getGroupId());
                    dependencies.setArtifactId(row, path.getArtifactId());
                    dependencies.setType(row, path.getType());
                    dependencies.setVersion(row, path.getVersion());
                }
            }
        });

        buttonsBar.addRelatedGap();
        buttonsBar.addFixed(browseDependencyButton);
    }

    private class DependencyBrowseDialog extends SelectRepositoryItemDialog {
        public DependencyBrowseDialog() {
            setAllowingArtifacts(false);
            setAllowingGroups(false);
            setAllowingRoot(false);
            setAllowingTypes(false);
            setAllowingVersions(true);
            setTitle(BROWSE_REPO_DLG_TITLE);
        }
    }
}
