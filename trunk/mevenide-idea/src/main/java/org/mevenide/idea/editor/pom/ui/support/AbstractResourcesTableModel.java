package org.mevenide.idea.editor.pom.ui.support;

import org.mevenide.idea.psi.project.AbstractPsiResources;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public abstract class AbstractResourcesTableModel<Psi extends AbstractPsiResources>
    extends AbstractBeanRowsTableModel<Psi> {

    private static final String[] COLUMN_TITLES = new String[]{
        "Directory",
        "Target Path"
    };

    protected AbstractResourcesTableModel(final Psi pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getDirectory(rowIndex);
            case 1:
                return model.getTargetPath(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setDirectory(rowIndex, value);
                break;
            case 1:
                model.setTargetPath(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("directory".equals(pProperyName))
            return 0;
        else if ("targetPath".equals(pProperyName))
            return 1;
        else
            return -1;
    }
}
