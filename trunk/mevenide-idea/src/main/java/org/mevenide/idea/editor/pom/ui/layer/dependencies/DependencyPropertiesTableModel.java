package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.psi.XmlTagTableModel;
import org.mevenide.idea.util.ui.table.CRUDTableModel;

/**
 * @author Arik
 */
public class DependencyPropertiesTableModel extends XmlTagTableModel implements CRUDTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DependencyPropertiesTableModel.class);

    private static final String UNKNOWN_PROPERTY_NAME = "Unknown";

    public DependencyPropertiesTableModel(final XmlFile pPsiFile) {
        super(pPsiFile);
    }


    protected void setTagValue(final XmlTag pTag,
                               final Object pValue,
                               final int pRow,
                               final int pColumn) {
        try {
            final XmlTag propsTag = ensureTag();
            final XmlTag[] propTags = propsTag.getSubTags();
            final XmlTag propTag;

            if (pRow >= propTags.length) {
                final String value;
                String propertyName;
                if (pColumn == 0) {
                    propertyName = pValue == null ? UNKNOWN_PROPERTY_NAME : pValue.toString();
                    if (propertyName.trim().length() == 0)
                        propertyName = UNKNOWN_PROPERTY_NAME;
                    value = null;
                }
                else {
                    propertyName = UNKNOWN_PROPERTY_NAME;
                    value = pValue == null ? null : pValue.toString();
                }

                propsTag.add(propsTag.createChildTag(propertyName,
                                                     propsTag.getNamespace(),
                                                     value,
                                                     false));
            }
            else {
                final String value = pValue == null ? null : pValue.toString();
                propTag = propTags[pRow];
                if(pColumn == 0)
                    propTag.setName(value);
                else
                    propTag.getValue().setText(value);
            }
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    protected Object getTagValue(final XmlTag pTag, final int pRow, final int pColumn) {
        final XmlTag propsTag = getTag();
        if (propsTag == null)
            return null;

        final XmlTag[] propTags = propsTag.getSubTags();
        final XmlTag propTag = propTags[pRow];
        if(pColumn == 0)
            return propTag.getName();
        else
            return propTag.getValue().getTrimmedText();
    }

    public int getRowCount() {
        final XmlTag tag = getTag();
        if (tag == null)  {
//            LOG.warn("ROW count is 0");
            return 0;
        }

        return tag.getSubTags().length;
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Value";
            default:
                throw new IndexOutOfBoundsException(column + "");
        }
    }

    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }

    public Object appendRow() {
        final RowAppenderRunnable addTagCmd = new RowAppenderRunnable();

        IDEUtils.runCommand(getTagPath().getFile().getProject(), addTagCmd);
        return addTagCmd.getResult();
    }

    public void removeRows(final int... pRowIndices) {
        final Runnable deleteTagsCmd = new Runnable() {
            public void run() {
                try {
                    final XmlTag containerTag = getTagPath().getTag();
                    if (containerTag == null)
                        return;

                    final XmlTag[] childTags = containerTag.getSubTags();
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

        IDEUtils.runCommand(getTagPath().getFile().getProject(), deleteTagsCmd);
    }

    private class RowAppenderRunnable implements Runnable {
        private XmlTag result = null;

        public void run() {
            try {
                final XmlTag containerTag = getTagPath().ensureTag();
                final String namespace = containerTag.getNamespace();
                final XmlTag childTag = containerTag.createChildTag(
                    UNKNOWN_PROPERTY_NAME,
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
