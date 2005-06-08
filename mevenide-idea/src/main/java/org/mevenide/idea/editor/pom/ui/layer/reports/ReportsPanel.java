package org.mevenide.idea.editor.pom.ui.layer.reports;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Table;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.reports.JDomReportsFinder;
import org.mevenide.environment.ILocationFinder;

/**
 * @author Arik
 */
public class ReportsPanel extends AbstractPomLayerPanel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ReportsPanel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ReportsPanel.class);

    /**
     * The reports list.
     */
    protected final String[] reports = findReports(file);

    /**
     * The table model for showing the reports.
     */
    private final TableModel model = new ReportsTableModel(file, reports);

    /**
     * The reports table.
     */
    private final JTable reportsTable = new Table(model);

    public ReportsPanel(final XmlFile pFile) {
        super(pFile);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        reportsTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private void layoutComponents() {
        final LabeledPanel labeledPanel = new LabeledPanel(
                RES.get("reports.desc"),
                ScrollPaneFactory.createScrollPane(reportsTable));

        setLayout(new BorderLayout());
        add(labeledPanel, BorderLayout.CENTER);
    }

    private static String[] findReports(final PsiFile pFile) {
        try {
            final VirtualFile virtualFile = pFile.getVirtualFile();
            final Project project = pFile.getProject();
            final Module module = VfsUtil.getModuleForFile(project, virtualFile);
            final ILocationFinder finder = new ModuleLocationFinder(module);
            final IReportsFinder reportsFinder = new JDomReportsFinder(finder);
            return reportsFinder.findReports();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new String[0];
        }
    }
}
