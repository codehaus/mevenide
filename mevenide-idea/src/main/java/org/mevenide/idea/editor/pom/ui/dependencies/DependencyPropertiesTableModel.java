package org.mevenide.idea.editor.pom.ui.dependencies;

import org.mevenide.idea.psi.project.PsiDependencyProperties;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class DependencyPropertiesTableModel
    extends AbstractBeanRowsTableModel<PsiDependencyProperties> {
    public DependencyPropertiesTableModel(final PsiDependencyProperties pModel) {
        super(pModel, new String[]{"Name", "Value"});
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        return -1;
    }

    public Object getValueAt(final int pRow, final int pColumn) {
        final String propertyName = model.getPropertyNames()[pRow];
        if (pColumn == 0)
            return propertyName;
        else
            return model.getProperty(propertyName);
    }

    @Override
    public void setValueAt(final Object pValue, final int pRow, final int pColumn) {
        final String[] propertyNames = model.getPropertyNames();
        final String propertyName = propertyNames[pRow];

        if (pColumn == 0) {
            final String value =
                pValue == null ?
                    model.getUnknownPropertyName() :
                    pValue.toString();

            model.renameProperty(propertyName, value);
        }
        else if (pColumn == 1) {
            final String value = pValue == null ? "" : pValue.toString();
            model.setProperty(propertyName, value);
        }
        else
            throw new IllegalArgumentException("Illegal column index.");
    }
}
