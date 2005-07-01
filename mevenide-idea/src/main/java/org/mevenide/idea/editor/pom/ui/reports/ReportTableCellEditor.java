package org.mevenide.idea.editor.pom.ui.reports;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.psi.project.PsiReports;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.reports.JDomReportsFinder;

/**
 * @author Arik
 */
public class ReportTableCellEditor extends AbstractCellEditor
    implements TableCellEditor, ActionListener {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ReportTableCellEditor.class);

    /**
     * The field for editing with a browse button.
     */
    private final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton(this);

    /**
     * The current value of the editor.
     */
    private String value = null;

    /**
     * The PSI model we use.
     */
    private final PsiReports model;
    private static final String TITLE = "Select a report";
    private static final SelectFromListDialog.ToStringAspect TO_STRING_ASPECT = new SelectFromListDialog.ToStringAspect() {
        public String getToStirng(
            Object obj) {
            return obj.toString();
        }
    };

    public ReportTableCellEditor(final PsiReports pModel) {
        model = pModel;
        field.setBorder(null);
        field.setOpaque(false);
        field.getTextField().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        field.getTextField().setOpaque(false);
    }

    public Component getTableCellEditorComponent(final JTable pTable,
                                                 final Object pValue,
                                                 final boolean pSelected,
                                                 final int pRow,
                                                 final int pColumn) {

        value = pValue == null ? null : pValue.toString();
        field.setText(value);
        return field;
    }

    public Object getCellEditorValue() {
        return value;
    }

    public void actionPerformed(final ActionEvent pEvent) {
        final XmlFile xmlFile = model.getXmlFile();
        final VirtualFile virtualFile = xmlFile.getVirtualFile();
        final Project project = xmlFile.getProject();
        final Module module = VfsUtil.getModuleForFile(project, virtualFile);
        final ILocationFinder finder = new ModuleLocationFinder(module);
        final IReportsFinder reportsFinder = new JDomReportsFinder(finder);

        String[] reports = findAvailableReports(reportsFinder);

        final SelectFromListDialog dlg = new SelectFromListDialog(
            project,
            reports,
            TO_STRING_ASPECT,
            TITLE,
            ListSelectionModel.SINGLE_SELECTION);

        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.setSelection(value);
        dlg.show();

        if (dlg.isOK()) {
            final Object[] selection = dlg.getSelection();

            if (selection != null && selection.length > 0)
                value = selection[0].toString();
            else
                value = null;

            field.setText(value);
        }
    }

    private String[] findAvailableReports(final IReportsFinder pReportsFinder) {
        try {
            return pReportsFinder.findReports();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new String[0];
        }
    }
}
