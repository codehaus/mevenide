package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.PsiTeamMembers;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class MembersPanel extends CRUDTablePanel<MembersTableModel> {
    protected MembersPanel(final PsiTeamMembers pPsi) {
        super(pPsi.getXmlFile(),
              new MembersTableModel<PsiTeamMembers>(pPsi));
    }
}
