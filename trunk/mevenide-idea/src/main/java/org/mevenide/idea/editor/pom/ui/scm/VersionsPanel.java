package org.mevenide.idea.editor.pom.ui.scm;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiVersions;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class VersionsPanel extends CRUDTablePanel {

    public VersionsPanel(final PsiProject pModel) {
        this(pModel.getVersions());
    }

    public VersionsPanel(final PsiVersions pModel) {
        super(pModel.getXmlFile(), new VersionsTableModel(pModel));
    }
}