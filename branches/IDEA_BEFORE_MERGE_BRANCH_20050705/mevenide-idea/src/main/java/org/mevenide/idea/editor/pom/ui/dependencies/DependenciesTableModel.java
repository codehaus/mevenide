package org.mevenide.idea.editor.pom.ui.dependencies;

import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class DependenciesTableModel extends AbstractBeanRowsTableModel<PsiDependencies> {
    private static final String[] COLUMN_TITLES = new String[]{
        "Group ID",
        "Artifact ID",
        "Version",
        "Type"
    };

    public DependenciesTableModel(final PsiProject pModel) {
        this(pModel.getDependencies());
    }

    public DependenciesTableModel(final PsiDependencies pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getGroupId(rowIndex);
            case 1:
                return model.getArtifactId(rowIndex);
            case 2:
                return model.getVersion(rowIndex);
            case 3:
                return model.getType(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setGroupId(rowIndex, value);
                break;
            case 1:
                model.setArtifactId(rowIndex, value);
                break;
            case 2:
                model.setVersion(rowIndex, value);
                break;
            case 3:
                model.setType(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("groupId".equals(pProperyName))
            return 0;
        else if ("artifactId".equals(pProperyName))
            return 1;
        else if ("version".equals(pProperyName))
            return 2;
        else if ("type".equals(pProperyName))
            return 3;
        else
            return -1;
    }
}
