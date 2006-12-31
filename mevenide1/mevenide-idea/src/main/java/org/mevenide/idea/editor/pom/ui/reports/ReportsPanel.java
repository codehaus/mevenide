package org.mevenide.idea.editor.pom.ui.reports;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class ReportsPanel extends CRUDTablePanel<ReportsTableModel> {
    public ReportsPanel(final PsiProject pModel) {
        super(pModel.getXmlFile(), new ReportsTableModel(pModel.getReports()));
        getComponent().setDefaultEditor(String.class,
                                        new ReportTableCellEditor(pModel.getReports()));
    }
}
