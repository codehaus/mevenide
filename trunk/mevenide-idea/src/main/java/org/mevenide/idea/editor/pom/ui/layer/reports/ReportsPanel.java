package org.mevenide.idea.editor.pom.ui.layer.reports;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.util.ui.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.reports.JDomReportsFinder;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class ReportsPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ReportsPanel.class);

    private final TableModel model = new ReportsTableModel(project,
                                                           document,
                                                           findReports());
    private final JTable reportsTable = new Table(model);

    public ReportsPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        reportsTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private void layoutComponents() {
        final LabeledPanel labeledPanel = new LabeledPanel(
                RES.get("reports.desc"),
                new JScrollPane(reportsTable));

        setLayout(new BorderLayout());
        add(labeledPanel, BorderLayout.CENTER);
    }

    private String[] findReports() {
        try {
            final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            final Module module = VfsUtil.getModuleForFile(project, file);
            final IReportsFinder reportsFinder = new JDomReportsFinder(new ModuleLocationFinder(module));
            return reportsFinder.findReports();
        }
        catch (Exception e) {
            final Log log = LogFactory.getLog(ReportsPanel.class);
            log.error(e.getMessage(), e);
            return new String[0];
        }
    }
}
