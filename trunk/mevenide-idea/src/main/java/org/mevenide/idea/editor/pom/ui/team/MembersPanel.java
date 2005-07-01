package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.AbstractPsiTeamMembers;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class MembersPanel extends CRUDTablePanel<MembersTableModel> {
    protected MembersPanel(final AbstractPsiTeamMembers pPsi) {
        super(pPsi.getXmlFile(),
              new MembersTableModel<AbstractPsiTeamMembers>(pPsi));
    }
}
