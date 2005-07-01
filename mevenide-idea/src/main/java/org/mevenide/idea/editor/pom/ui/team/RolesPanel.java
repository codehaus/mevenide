package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.PsiTeamMemberRoles;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class RolesPanel extends CRUDTablePanel<RolesTableModel> {
    public RolesPanel(final PsiTeamMemberRoles pRoles) {
        super(pRoles.getXmlFile(), new RolesTableModel(pRoles));
    }
}
