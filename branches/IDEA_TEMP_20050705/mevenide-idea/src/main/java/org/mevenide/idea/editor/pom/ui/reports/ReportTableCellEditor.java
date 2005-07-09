package org.mevenide.idea.editor.pom.ui.reports;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.reports.MavenReportManager;
import org.mevenide.idea.project.reports.Report;
import org.mevenide.idea.psi.project.PsiReports;
import org.mevenide.idea.util.ui.MultiLineLabel;

/**
 * @author Arik
 */
public class ReportTableCellEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ReportTableCellEditor.class);

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
        public String getToStirng(Object obj) {
            if (obj instanceof Report) {
                final Report report = (Report) obj;
                final StringBuilder buf = new StringBuilder(report.getName());
                buf.append(" - ").append(report.getId()).append(": ");
                buf.append(report.getDescription());
                return buf.toString();
            }
            else
                return obj == null ? "Unknown" : obj.toString();
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
        final Project project = model.getXmlFile().getProject();
        final MavenReportManager mgr = MavenReportManager.getInstance(project);
        final Report[] reports = mgr.getReports();

        final SelectFromListDialog dlg = new SelectFromListDialog(
                project,
                reports,
                TO_STRING_ASPECT,
                TITLE,
                ListSelectionModel.SINGLE_SELECTION);

        dlg.addToDialog(new MultiLineLabel(RES.get("select.report.label")),
                        BorderLayout.PAGE_START);

        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.setSelection(value);
        dlg.show();

        if (dlg.isOK()) {
            final Object[] selection = dlg.getSelection();

            if (selection != null && selection.length > 0)
                value = ((Report) selection[0]).getId();
            else
                return;

            field.setText(value);
        }
    }
}
