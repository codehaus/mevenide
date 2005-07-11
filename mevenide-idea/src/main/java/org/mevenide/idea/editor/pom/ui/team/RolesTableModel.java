package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.PsiTeamMemberRoles;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class RolesTableModel extends AbstractBeanRowsTableModel<PsiTeamMemberRoles> {
    private static final String[] COLUMN_TITLES = new String[]{
            "Role"
    };


    public RolesTableModel(final PsiTeamMemberRoles pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getRole(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setRole(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        return -1;
    }

}
