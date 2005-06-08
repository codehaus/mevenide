package org.mevenide.idea.util.psi;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;

/**
 * This table model wraps XML data in the notion of a container tag with
 * several "row" tags
 * @author Arik
 */
public abstract class XmlTagTableModel extends AbstractPsiTableModel<XmlFile> {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(XmlTagTableModel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(XmlTagTableModel.class);

    /**
     * The path to the tag.
     */
    private XmlTagPath path;

    /**
     * Creates an model that will update the specified tag path in the given
     * XML file.
     *
     * @param pPsiFile the xml file to update
     * @param pTagPath the path to the tag this table model will update
     */
    public XmlTagTableModel(final XmlFile pPsiFile,
                            final String pTagPath) {
        this(pPsiFile, new XmlTagPath(pPsiFile, pTagPath));
    }

    /**
     * Creates an model that will update the specified tag path in the given
     * XML file.
     *
     * @param pPsiFile the xml file to update
     * @param pTagPath the path to the tag this table model will update
     */
    public XmlTagTableModel(final XmlFile pPsiFile,
                            final XmlTagPath pTagPath) {
        super(pPsiFile);
        setTagPath(pTagPath);
    }

    /**
     * Returns the tag path this model is reading/updating.
     *
     * @return tag path
     */
    public final XmlTagPath getTagPath() {
        return path;
    }

    /**
     * Sets the tag path for this model, and refreshes the model from it.
     *
     * @param pTagPath the tag path to use
     */
    public final void setTagPath(final XmlTagPath pTagPath) {
        if(pTagPath == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pTagPath"));
        path = pTagPath;
        refreshModel();
    }

    protected final void setValueAtInternal(final Object pValue,
                                            final int pRow,
                                            final int pColumn) {
        try {
            setTagValue(path.ensureTag(), pValue, pRow, pColumn);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * This method should sets the given value in the model's tag, in
     * the appropriate location for the specified row and column.
     *
     * <p>It is up to the implementing class to decide how to represent
     * the data inside the given tag (based on the row and column).</p>
     *
     * @param pTag the model's tag
     * @param pValue the value to set
     * @param pRow the row to update
     * @param pColumn the column to update
     */
    protected abstract void setTagValue(final XmlTag pTag,
                                        final Object pValue,
                                        final int pRow,
                                        final int pColumn);

    public final Object getValueAt(final int pRow, final int pColumn) {
        final XmlTag tag = path.getTag();
        if(tag == null)
            return null;

        return getTagValue(tag, pRow, pColumn);
    }

    /**
     * This method should return the value for the given row and column
     * from the model's tag (given).
     *
     * <p>If the model's tag does not exist (based on the model's tag
     * path expression), this method will not be called. Therefor the
     * tag will never be {@code null}.</p>
     *
     * @param pTag the tag to retrieve the value from (never {@code null})
     * @param pRow the row to retrieve the vlaue for
     * @param pColumn the column to retrieve the value for
     * @return the value
     */
    protected abstract Object getTagValue(final XmlTag pTag,
                                          final int pRow,
                                          final int pColumn);
}
