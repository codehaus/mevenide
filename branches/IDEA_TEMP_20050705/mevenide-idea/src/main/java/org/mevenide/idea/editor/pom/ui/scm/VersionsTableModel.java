package org.mevenide.idea.editor.pom.ui.scm;

import org.mevenide.idea.psi.project.PsiVersions;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class VersionsTableModel extends AbstractBeanRowsTableModel<PsiVersions> {
    private static final String[] COLUMN_TITLES = new String[]{
        "ID",
        "Name",
        "Tag"
    };

    public VersionsTableModel(final PsiVersions pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getId(rowIndex);
            case 1:
                return model.getName(rowIndex);
            case 2:
                return model.getTag(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setId(rowIndex, value);
                break;
            case 1:
                model.setName(rowIndex, value);
                break;
            case 2:
                model.setTag(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("id".equals(pProperyName))
            return 0;
        if ("name".equals(pProperyName))
            return 1;
        if ("tag".equals(pProperyName))
            return 2;
        else
            return -1;
    }
}
