package org.mevenide.idea.editor.pom.ui.reports;

import com.intellij.psi.xml.XmlFile;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.PsiReports;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class ReportsPanel extends LabeledPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ReportsPanel.class);

    public ReportsPanel(final PsiReports pModel) {
        super(RES.get("reports.desc"), createReportsPanel(pModel));
        final CRUDTablePanel<ReportsTableModel> c = (CRUDTablePanel<ReportsTableModel>) component;
        final JTable table = c.getComponent();
        table.setDefaultEditor(String.class, new ReportTableCellEditor(pModel));
    }

    private static CRUDTablePanel<ReportsTableModel> createReportsPanel(final PsiReports pModel) {
        final XmlFile xmlFile = pModel.getXmlFile();
        final ReportsTableModel model = new ReportsTableModel(pModel);
        final CRUDTablePanel<ReportsTableModel> tablePanel;
        tablePanel = new CRUDTablePanel<ReportsTableModel>(
            xmlFile,
            model);

        return tablePanel;
    }
}
