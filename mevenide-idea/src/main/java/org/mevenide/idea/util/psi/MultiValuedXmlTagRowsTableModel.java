package org.mevenide.idea.util.psi;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public class MultiValuedXmlTagRowsTableModel extends XmlTagRowsTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(MultiValuedXmlTagRowsTableModel.class);

    /**
     * The names of the value tags. Each tag name corresponds to the
     * tag that will receive the values for the column at its index in
     * the array.
     */
    private final String[] valueTagNames;

    public MultiValuedXmlTagRowsTableModel(final XmlFile pPsiFile,
                                           final String pTagPath,
                                           final String pRowTagName,
                                           final String[] pValueTagNames) {
        this(new XmlTagPath(pPsiFile, pTagPath),
             pRowTagName,
             pValueTagNames);
    }

    public MultiValuedXmlTagRowsTableModel(final XmlTagPath pTagPath,
                                           final String pRowTagName,
                                           final String[] pValueTagNames) {
        super(pTagPath, pRowTagName);
        valueTagNames = pValueTagNames;
    }

    protected Object getTagRowValue(final XmlTag pTag, final int pColumn) {
        final XmlTag columnTag = pTag.findFirstSubTag(valueTagNames[pColumn]);
        if(columnTag == null)
            return null;

        return columnTag.getValue().getTrimmedText();
    }

    protected void setTagRowValue(final XmlTag pTag, final Object pValue, final int pColumn) {
        XmlTag columnTag = pTag.findFirstSubTag(valueTagNames[pColumn]);
        if (columnTag == null) {
            try {
                final XmlTag childTag = pTag.createChildTag(valueTagNames[pColumn],
                                                            pTag.getNamespace(),
                                                            null,
                                                            false);
                columnTag = (XmlTag) pTag.add(childTag);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
                return;
            }
        }

        final String value = pValue == null ? null : pValue.toString();
        columnTag.getValue().setText(value);
    }

    public int getColumnCount() {
        return valueTagNames.length;
    }
}
