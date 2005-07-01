package org.mevenide.idea.editor.pom.ui.scm;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiScmBranches;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class BranchesPanel extends CRUDTablePanel {
    public BranchesPanel(final PsiProject pModel) {
        this(pModel.getScmBranches());
    }

    public BranchesPanel(final PsiScmBranches pModel) {
        super(pModel.getXmlFile(), new BranchesTableModel(pModel));
    }
}
