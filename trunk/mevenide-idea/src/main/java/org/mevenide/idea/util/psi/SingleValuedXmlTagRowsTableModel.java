package org.mevenide.idea.util.psi;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

/**
 * @author Arik
 */
public class SingleValuedXmlTagRowsTableModel extends XmlTagRowsTableModel {

    private final String columnName;

    public SingleValuedXmlTagRowsTableModel(final XmlFile pPsiFile,
                                            final String pTagPath,
                                            final String pRowTagName,
                                            final String pColumnTitle) {
        super(pPsiFile, pTagPath, pRowTagName);
        columnName = pColumnTitle;
    }

    public SingleValuedXmlTagRowsTableModel(final XmlFile pPsiFile,
                                            final XmlTagPath pTagPath,
                                            final String pRowTagName,
                                            final String pColumnTitle) {
        super(pPsiFile, pTagPath, pRowTagName);
        columnName = pColumnTitle;
    }

    protected Object getTagRowValue(final XmlTag pTag, final int pColumn) {
        return pTag.getValue().getTrimmedText();
    }

    protected void setTagRowValue(final XmlTag pTag,
                                  final Object pValue,
                                  final int pColumn) {
        pTag.getValue().setText(pValue == null ? null : pValue.toString());
    }

    public int getColumnCount() {
        return 1;
    }

    public String getColumnName(int column) {
        return columnName;
    }
}
