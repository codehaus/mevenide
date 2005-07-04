package org.mevenide.idea.editor.pom.ui.support;

import org.mevenide.idea.psi.project.PatternType;
import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class AbstractResourcePatternsTableModel
        extends AbstractBeanRowsTableModel<PsiResourcePatterns> {
    public AbstractResourcePatternsTableModel(final PsiResourcePatterns pModel) {
        super(pModel, getColumnTitles(pModel.getType()));
    }

    private static String[] getColumnTitles(final PatternType pType) {
        if (pType == PatternType.INCLUDES)
            return new String[]{"Includes"};
        else
            return new String[]{"Excludes"};
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getPattern(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setPattern(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("pattern".equals(pProperyName))
            return 0;
        else
            return -1;
    }
}
