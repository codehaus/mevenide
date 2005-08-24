package org.mevenide.idea.editor.pom.ui.dependencies;

import org.mevenide.idea.psi.project.PsiDependencyProperties;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class DependencyPropertiesTablePanel
        extends CRUDTablePanel<DependencyPropertiesTableModel> {
    public DependencyPropertiesTablePanel(final PsiDependencyProperties pModel) {
        super(pModel.getXmlFile(), new DependencyPropertiesTableModel(pModel));
    }
}
