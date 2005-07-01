package org.mevenide.idea.util.ui.table;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.psi.MultiValuedXmlTagRowsTableModel;
import org.mevenide.idea.psi.util.XmlTagPath;

/**
 * @author Arik
 * @todo rename to SimpleXmlCRUDTablePanel
 */
public class SimpleCRUDTablePanel extends CRUDTablePanel {
    public SimpleCRUDTablePanel(final XmlFile pXmlFile,
                                final String pTagPath,
                                final String pRowTagName,
                                final String[] pValueTagNames,
                                final String[] pColumnTitles) {
        super(pXmlFile,
              new SimpleCRUDTableModel(pXmlFile,
                                       pTagPath,
                                       pRowTagName,
                                       pValueTagNames,
                                       pColumnTitles));
    }

    public SimpleCRUDTablePanel(final XmlTagPath pTagPath,
                                final String pRowTagName,
                                final String[] pValueTagNames,
                                final String[] pColumnTitles) {
        super(pTagPath.getFile(),
              new SimpleCRUDTableModel(pTagPath,
                                       pRowTagName,
                                       pValueTagNames,
                                       pColumnTitles));
    }


    private static class SimpleCRUDTableModel extends MultiValuedXmlTagRowsTableModel {
        private final String[] columnTitles;

        public SimpleCRUDTableModel(final XmlFile pPsiFile,
                                    final String pTagPath,
                                    final String pRowTagName,
                                    final String[] pValueTagNames,
                                    final String[] pColumnTitles) {
            super(pPsiFile, pTagPath, pRowTagName, pValueTagNames);
            columnTitles = pColumnTitles;
        }

        public SimpleCRUDTableModel(final XmlTagPath pTagPath,
                                    final String pRowTagName,
                                    final String[] pValueTagNames,
                                    final String[] pColumnTitles) {
            super(pTagPath, pRowTagName, pValueTagNames);
            columnTitles = pColumnTitles;
        }

        public String getColumnName(int column) {
            return columnTitles[column];
        }
    }
}
