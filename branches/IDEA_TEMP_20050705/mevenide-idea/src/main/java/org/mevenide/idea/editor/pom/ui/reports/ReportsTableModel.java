package org.mevenide.idea.editor.pom.ui.reports;

import org.mevenide.idea.psi.project.PsiReports;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class ReportsTableModel extends AbstractBeanRowsTableModel<PsiReports> {
    private static final String[] COLUMN_TITLES = new String[]{
        "Report Name"
    };

    public ReportsTableModel(final PsiReports pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getReport(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setReport(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("report".equals(pProperyName))
            return 0;
        else
            return -1;
    }
}
