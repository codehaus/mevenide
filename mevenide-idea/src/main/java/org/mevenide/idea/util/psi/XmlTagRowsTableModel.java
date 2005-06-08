package org.mevenide.idea.util.psi;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.table.CRUDTableModel;
import org.mevenide.idea.util.IDEUtils;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Arik
 */
public abstract class XmlTagRowsTableModel extends XmlTagTableModel implements CRUDTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(XmlTagRowsTableModel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(XmlTagRowsTableModel.class);

    private String rowTagName;

    public XmlTagRowsTableModel(final XmlFile pPsiFile,
                                final String pTagPath,
                                final String pRowTagName) {
        this(pPsiFile, new XmlTagPath(pPsiFile, pTagPath), pRowTagName);
    }

    public XmlTagRowsTableModel(final XmlFile pPsiFile,
                                final XmlTagPath pTagPath,
                                final String pRowTagName) {
        super(pPsiFile, pTagPath);
        setRowTagName(pRowTagName);
    }

    public String getRowTagName() {
        return rowTagName;
    }

    public void setRowTagName(final String pRowTagName) {
        if (pRowTagName == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pRowTagName"));
        rowTagName = pRowTagName;
    }

    public int getRowCount() {
        final XmlTag tag = getTagPath().getTag();
        return tag == null ? 0 : tag.findSubTags(rowTagName).length;
    }

    protected final Object getTagValue(final XmlTag pTag, final int pRow, final int pColumn) {
        final XmlTag[] rowTags = pTag.findSubTags(rowTagName);
        if (pRow >= rowTags.length)
            return null;

        final XmlTag rowTag = rowTags[pRow];
        return getTagRowValue(rowTag, pColumn);
    }

    protected abstract Object getTagRowValue(final XmlTag pTag,
                                             final int pColumn);

    protected final void setTagValue(final XmlTag pTag,
                                     final Object pValue,
                                     final int pRow,
                                     final int pColumn) {
        final XmlTag[] rowTags = pTag.findSubTags(rowTagName);
        if (pRow >= rowTags.length) {
            final String namespace = pTag.getNamespace();
            final XmlTag newTag = pTag.createChildTag(rowTagName,
                                                      namespace,
                                                      null,
                                                      false);
            try {
                final XmlTag rowTag = (XmlTag) pTag.add(newTag);
                setTagRowValue(rowTag, pValue, pColumn);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        else {
            final XmlTag rowTag = rowTags[pRow];
            setTagRowValue(rowTag, pValue, pColumn);
        }
    }

    protected abstract void setTagRowValue(final XmlTag pTag,
                                           final Object pValue,
                                           final int pColumn);

    public XmlTag appendRow() {
        final RowAppenderRunnable addTagCmd = new RowAppenderRunnable();

        IDEUtils.runCommand(getTagPath().getFile().getProject(), addTagCmd);
        return addTagCmd.getResult();
    }

    public void removeRows(final int... pRowIndices) {
        final Runnable deleteTagsCmd = new Runnable() {
            public void run() {
                try {
                    final XmlTag containerTag = getTagPath().getTag();
                    if(containerTag == null)
                        return;

                    final XmlTag[] childTags = containerTag.findSubTags(rowTagName);
                    final Set<XmlTag> tags = new HashSet<XmlTag>(pRowIndices.length);
                    for (int i = 0; i < pRowIndices.length; i++)
                        tags.add(childTags[pRowIndices[i]]);

                    for (XmlTag xmlTag : tags)
                        xmlTag.delete();
                }
                catch (IncorrectOperationException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };

        IDEUtils.runCommand(getTagPath().getFile().getProject(),
                            deleteTagsCmd);
    }

    private class RowAppenderRunnable implements Runnable {
        private XmlTag result = null;

        public void run() {
            try {
                final XmlTag containerTag = getTagPath().ensureTag();
                final String namespace = containerTag.getNamespace();
                final XmlTag childTag = containerTag.createChildTag(rowTagName,
                                                                    namespace,
                                                                    null,
                                                                    false);
                result = (XmlTag) containerTag.add(childTag);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        public XmlTag getResult() {
            return result;
        }
    }
}
