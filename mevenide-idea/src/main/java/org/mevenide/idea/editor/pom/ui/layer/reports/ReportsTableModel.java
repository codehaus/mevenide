package org.mevenide.idea.editor.pom.ui.layer.reports;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.psi.XmlTagPath;
import org.mevenide.idea.util.psi.XmlTagTableModel;

/**
 * @author Arik
 */
public class ReportsTableModel extends XmlTagTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ReportsTableModel.class);

    /**
     * The list of available Maven reports.
     */
    private final String[] reports;

    public ReportsTableModel(final XmlFile pFile, final String[] pReports) {
        super(pFile, new XmlTagPath(pFile, "project/reports"));
        reports = pReports;
    }

    protected void setTagValue(final XmlTag pTag,
                               final Object pValue,
                               final int pRow,
                               final int pColumn) {
        if (pColumn != 0)
            throw new IllegalStateException("Readonly column " + pColumn);

        try {
            final XmlTag[] reportTags = pTag.findSubTags("report");
            final String report = reports[pRow];

            final boolean select =
                pValue instanceof Boolean ? (Boolean) pValue : false;

            for (final XmlTag reportTag : reportTags) {
                final String tagReport = reportTag.getValue().getTrimmedText();
                if (report.equals(tagReport)) {
                    if(!select)
                        reportTag.delete();
                    return;
                }
            }

            pTag.add(pTag.createChildTag("report",
                                         pTag.getNamespace(),
                                         report,
                                         false));
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    protected Object getTagValue(final XmlTag pTag,
                                 final int pRow,
                                 final int pColumn) {
        switch (pColumn) {
            case 0:
                return isSelected(pRow);
            case 1:
                return reports[pRow];
            default:
                throw new IllegalArgumentException("Illegal column " + pColumn);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("Illegal row " + columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return " ";
            case 1:
                return "Report";
            default:
                throw new IllegalArgumentException("Illegal row " + column);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return reports.length;
    }

    public final boolean isSelected(final int pReportIndex) {
        return isSelected(reports[pReportIndex]);
    }

    protected boolean isSelectedInternal(final XmlTag pReportsTag,
                                         final String pReport) {
        if(pReportsTag == null)
            return false;

        final XmlTag[] reportTags = pReportsTag.findSubTags("report");
        if (reportTags == null || reportTags.length == 0)
            return false;

        for (final XmlTag reportTag : reportTags) {
            final String report = reportTag.getValue().getTrimmedText();
            if (pReport.equals(report))
                return true;
        }

        return false;
    }

    public final boolean isSelected(final String pReport) {
        return isSelectedInternal(getTagPath().getTag(), pReport);
    }
}
