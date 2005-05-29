package org.mevenide.idea.editor.pom.ui.layer.reports;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.psi.PsiEventType;
import org.mevenide.idea.util.ui.table.AbstractXmlPsiTableModel;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
public class ReportsTableModel extends AbstractXmlPsiTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ReportsTableModel.class);

    private final String[] reports;

    public ReportsTableModel(final Project pProject, final Document pIdeaDocument, final String[] pReports) {
        super(pProject, pIdeaDocument);
        reports = pReports;
    }

    protected void setValueAtInternal(final Object pValue,
                                      final int pRow,
                                      final int pColumn) {
        if(pColumn != 0)
            throw new IllegalStateException("Readonly column " + pColumn);

        final Runnable command = new Runnable() {
            public void run() {
                try {
                    final XmlDocument doc = xmlFile.getDocument();
                    if(doc == null)
                        return;

                    final XmlTag projectTag = doc.getRootTag();
                    if(projectTag == null)
                        return;

                    XmlTag reportsTag = projectTag.findFirstSubTag("reports");
                    if(reportsTag == null)
                        reportsTag = (XmlTag) projectTag.add(
                                projectTag.createChildTag("reports",
                                                          projectTag.getNamespace(),
                                                          null,
                                                          false));

                    final XmlTag[] reportTags = reportsTag.findSubTags("report");
                    final String report = reports[pRow];

                    final boolean select = (Boolean)pValue;
                    if(select) {
                        for(final XmlTag reportTag : reportTags) {
                            final String tagReport = reportTag.getValue().getTrimmedText();
                            if(report.equals(tagReport))
                                return;
                        }
                        reportsTag.add(reportsTag.createChildTag("report",
                                                                 reportsTag.getNamespace(),
                                                                 report,
                                                                 false));
                    }
                    else {
                        for(final XmlTag reportTag : reportTags) {
                            final String tagReport = reportTag.getValue().getTrimmedText();
                            if(report.equals(tagReport)) {
                                reportTag.delete();
                                return;
                            }
                        }
                    }
                }
                catch (IncorrectOperationException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };

        IDEUtils.runCommand(project, command);
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("Illegal row " + columnIndex);
        }
    }

    @Override public String getColumnName(int column) {
        switch(column) {
            case 0:
                return "";
            case 1:
                return "Report";
            default:
                throw new IllegalArgumentException("Illegal row " + column);
        }
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return reports.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
                return isSelected(rowIndex);
            case 1:
                return reports[rowIndex];
            default:
                throw new IllegalArgumentException("Illegal column " + columnIndex);
        }
    }

    public final boolean isSelected(final int pReportIndex) {
        return isSelected(reports[pReportIndex]);
    }

    public boolean isSelected(final String pReport) {
        final XmlDocument doc = xmlFile.getDocument();
        if(doc == null)
            return false;

        final XmlTag projectTag = doc.getRootTag();
        if(projectTag == null)
            return false;

        final XmlTag reportsTag = projectTag.findFirstSubTag("reports");
        if(reportsTag == null)
            return false;

        final XmlTag[] reportTags = reportsTag.findSubTags("report");
        if(reportTags == null || reportTags.length == 0)
            return false;

        for(final XmlTag reportTag : reportTags) {
            final String report = reportTag.getValue().getTrimmedText();
            if(pReport.equals(report))
                return true;
        }

        return false;
    }

    public void refreshModel(PsiEventType pEventType, PsiTreeChangeEvent pEvent) {
        fireTableDataChanged();
    }
}
