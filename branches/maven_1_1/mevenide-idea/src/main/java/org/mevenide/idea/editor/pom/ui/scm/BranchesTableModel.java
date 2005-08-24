package org.mevenide.idea.editor.pom.ui.scm;

import org.mevenide.idea.psi.project.PsiScmBranches;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class BranchesTableModel extends AbstractBeanRowsTableModel<PsiScmBranches> {
    private static final String[] COLUMN_TITLES = new String[]{"Tag"};

    public BranchesTableModel(final PsiScmBranches pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
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
                model.setTag(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("tag".equals(pProperyName))
            return 0;
        else
            return -1;
    }
}
