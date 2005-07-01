package org.mevenide.idea.util.psi;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.util.XmlTagPath;

/**
 * This table model wraps XML data in the notion of a container tag with several "row"
 * tags
 *
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
     * Creates a model for the given XML file, with no tag path (will be empty until the
     * path is set via {@link #setTagPath(XmlTagPath)}).
     *
     * @param pPsiFile the xml file to update
     */
    public XmlTagTableModel(final XmlFile pPsiFile) {
        super(pPsiFile);
    }

    /**
     * Creates a model that will update the specified tag path in the given XML file.
     *
     * @param pPsiFile the xml file to update
     * @param pTagPath the path to the tag this table model will update
     */
    public XmlTagTableModel(final XmlFile pPsiFile,
                            final String pTagPath) {
        this(new XmlTagPath(pPsiFile, pTagPath));
    }

    /**
     * Creates a model that will update the specified tag path in the given XML file
     * (project is derived from the tag path object).
     *
     * @param pTagPath the path to the tag this table model will update
     */
    public XmlTagTableModel(final XmlTagPath pTagPath) {
        super(pTagPath.getFile());
        setTagPath(pTagPath);
    }

    /**
     * Returns the tag path this model is reading/updating.
     *
     * @return tag path
     */
    protected final XmlTagPath getTagPath() {
        return path;
    }

    protected final XmlTag getTag() {
        if (path == null)
            return null;

        return path.getTag();
    }

    protected final XmlTag ensureTag() throws IncorrectOperationException {
        if (path == null)
            throw new IllegalStateException(RES.get("missing.tag.path"));

        return path.ensureTag();
    }

    /**
     * Sets the tag path for this projectModel, and refreshes the model from it.
     *
     * @param pTagPath the tag path to use
     */
    public final void setTagPath(final String pTagPath) {
        if (pTagPath == null)
            setTagPath((XmlTagPath) null);
        else
            setTagPath(new XmlTagPath(psiFile, pTagPath));
    }

    private void setTagPath(final XmlTagPath pTagPath) {
        path = pTagPath;
        refreshModel();
    }

    protected final void setValueAtInternal(final Object pValue,
                                            final int pRow,
                                            final int pColumn) {
        if (path == null)
            throw new IllegalStateException(RES.get("missing.tag.path"));

        try {
            setTagValue(path.ensureTag(), pValue, pRow, pColumn);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * This method should sets the given value in the model's tag, in the appropriate
     * location for the specified row and column.
     *
     * <p>It is up to the implementing class to decide how to represent the data inside
     * the given tag (based on the row and column).</p>
     *
     * @param pTag    the model's tag
     * @param pValue  the value to set
     * @param pRow    the row to update
     * @param pColumn the column to update
     */
    protected abstract void setTagValue(final XmlTag pTag,
                                        final Object pValue,
                                        final int pRow,
                                        final int pColumn);

    public final Object getValueAt(final int pRow, final int pColumn) {
        if (path == null)
            return null;

        final XmlTag tag = path.getTag();
        if (tag == null)
            return null;

        return getTagValue(tag, pRow, pColumn);
    }

    /**
     * This method should return the value for the given row and column from the model's
     * tag (given).
     *
     * <p>If the projectModel's tag does not exist (based on the model's tag path
     * expression), this method will not be called. Therefor the tag will never be {@code
     * null}.</p>
     *
     * @param pTag    the tag to retrieve the value from (never {@code null})
     * @param pRow    the row to retrieve the vlaue for
     * @param pColumn the column to retrieve the value for
     *
     * @return the value
     */
    protected abstract Object getTagValue(final XmlTag pTag,
                                          final int pRow,
                                          final int pColumn);
}
