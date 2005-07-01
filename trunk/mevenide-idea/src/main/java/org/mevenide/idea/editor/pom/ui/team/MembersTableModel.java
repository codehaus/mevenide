package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.AbstractPsiTeamMembers;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class MembersTableModel<Psi extends AbstractPsiTeamMembers>
    extends AbstractBeanRowsTableModel<Psi> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(MembersTableModel.class);

    private static final String[] COLUMN_TITLES = new String[]{
        RES.get("name.col.title"),
        RES.get("id.col.title"),
        RES.get("email.col.title"),
        RES.get("org.col.title"),
        RES.get("url.col.title"),
        RES.get("timezone.col.title")};


    public MembersTableModel(final Psi pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getName(rowIndex);
            case 1:
                return model.getId(rowIndex);
            case 2:
                return model.getEmail(rowIndex);
            case 3:
                return model.getOrganization(rowIndex);
            case 4:
                return model.getUrl(rowIndex);
            case 5:
                return model.getTimezone(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setName(rowIndex, value);
                break;
            case 1:
                model.setId(rowIndex, value);
                break;
            case 2:
                model.setEmail(rowIndex, value);
                break;
            case 3:
                model.setOrganization(rowIndex, value);
                break;
            case 4:
                model.setUrl(rowIndex, value);
                break;
            case 5:
                model.setTimezone(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("name".equals(pProperyName))
            return 0;
        else if ("id".equals(pProperyName))
            return 1;
        else if ("email".equals(pProperyName))
            return 2;
        else if ("organization".equals(pProperyName))
            return 3;
        else if ("url".equals(pProperyName))
            return 4;
        else if ("timezone".equals(pProperyName))
            return 5;
        else
            return -1;
    }
}
