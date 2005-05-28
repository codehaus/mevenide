package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public abstract class TagBasedXmlPsiTableModel extends AbstractXmlPsiTableModel implements MutableXmlPsiTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(TagBasedXmlPsiTableModel.class);

    /**
     * The name of the container tag, which contains all rows. Each tag inside this tag represents a
     * row.
     *
     * @see #rowTagName
     */
    protected String[] containerTagPath;

    /**
     * The name of the tag which represents a single row.
     */
    protected String rowTagName;

    /**
     * Creates an instance for the given project and document.
     *
     * @param pProject the project the document belongs to
     * @param pIdeaDocument the PSI document backing the model
     */
    public TagBasedXmlPsiTableModel(final Project pProject,
                                    final Document pIdeaDocument) {
        this(pProject, pIdeaDocument, null, null);
    }

    /**
     * Creates an instance for the given project and document.
     *
     * <p>You must specify the name of the data container tag and the
     * name of the row tags. The container tag is a single tag (cannot
     * repeat itself) which contains multiple tags of the same name -
     * that name is the name of the row tags.</p>
     *
     * @param pProject the project the document belongs to
     * @param pIdeaDocument the PSI document backing the model
     * @param pContainerTagPath the name of the container tag
     * @param pRowTagName the name of the row tags
     */
    public TagBasedXmlPsiTableModel(final Project pProject,
                                    final Document pIdeaDocument,
                                    final String pContainerTagPath,
                                    final String pRowTagName) {
        super(pProject, pIdeaDocument);
        setTagPath(pContainerTagPath, pRowTagName);
    }

    public final String[] getContainerTagPath() {
        return containerTagPath;
    }

    public final void setContainerTagPath(final String pContainerTagPath) {
        setTagPath(pContainerTagPath, rowTagName);
    }

    public final void setContainerTagPath(final String[] pContainerTagPath) {
        setTagPath(pContainerTagPath, rowTagName);
    }

    public final String getRowTagName() {
        return rowTagName;
    }

    public final void setRowTagName(final String pRowTagName) {
        setTagPath(containerTagPath, pRowTagName);
    }

    public final void setTagPath(final String pContainerTagPath, final String pRowTagName) {
        final String[] containerPath = pContainerTagPath == null ? null : pContainerTagPath.split("/");
        setTagPath(containerPath, pRowTagName);
    }

    public void setTagPath(final String[] pContainerTagPath, final String pRowTagName) {
        containerTagPath = (pContainerTagPath == null) ? null : pContainerTagPath;
        rowTagName = pRowTagName;
        refreshModel();
    }

    public int getRowCount() {
        final XmlTag containerTag = findContainerTag();
        if(containerTag == null)
            return 0;

        return containerTag.findSubTags(rowTagName).length;
    }

    /**
     * Finds and returns the container tag.
     *
     * <p>The container tag holds multiple instances of row tags. For
     * instance, in POM dependencies, the container tag would be the
     * {@code <dependencies>} tag, and the row tag would be the
     * {@code <dependency>} tag.</p>
     *
     * <p>If the {@code pCreateIfNotFound} parameter is {@code true},
     * and the container tag does not exist, this method will try
     * to create it.</p>
     *
     * @param pCreateIfNotFound create the tag if it doesn't exist
     * @return a PSI xml tag
     */
    public XmlTag findContainerTag(final boolean pCreateIfNotFound)
            throws IncorrectOperationException {
        if(containerTagPath == null)
            return null;

        final XmlDocument xmlDocument = xmlFile.getDocument();
        if (xmlDocument == null)
            return null;

        final XmlTag projectTag = xmlDocument.getRootTag();
        if (projectTag == null)
            return null;

        final PsiManager mgr = PsiManager.getInstance(project);
        final PsiElementFactory factory = mgr.getElementFactory();
        XmlTag tag = projectTag;
        for (final String tagExpression : containerTagPath) {
            XmlTag currentTag;
            final int bracketStart = tagExpression.indexOf('[');
            final int bracketEnd = tagExpression.indexOf(']', bracketStart);
            if(bracketStart >= 0 && bracketEnd >= bracketStart) {
                //
                //the tag is an expression - parse the requested index
                //
                final int index = Integer.parseInt(tagExpression.substring(bracketStart + 1, bracketEnd));
                final String tagName = tagExpression.substring(0, bracketStart);
                final XmlTag[] subTags = tag.findSubTags(tagName);
                if(index < 0 || index >= subTags.length) {
                    LOG.warn("Container tag '" + tagName + "' for index '" + index + "' is illegal.");
                    return null;
                }

                currentTag = subTags[index];
            }
            else
                currentTag = tag.findFirstSubTag(tagExpression);

            if (currentTag == null) {
                if(!pCreateIfNotFound)
                    return null;

                currentTag = factory.createTagFromText("<" + tagExpression + "/>");
                tag = (XmlTag) tag.add(currentTag);
            }
            else
                tag = currentTag;
        }

        return tag;
    }

    /**
     * Finds and returns the container tag.
     *
     * <p>The container tag holds multiple instances of row tags. For
     * instance, in POM dependencies, the container tag would be the
     * {@code <dependencies>} tag, and the row tag would be the
     * {@code <dependency>} tag.</p>
     *
     * <p>If the tag does not exist, this method will return {@code null}.
     * </p>
     *
     * @return a PSI xml tag
     */
    public final XmlTag findContainerTag() {
        try {
            return findContainerTag(false);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    protected final void setValueAtInternal(final Object pValue,
                                            final int pRow,
                                            final int pColumn) {
        final XmlTag containerTag = findContainerTag();
        if(containerTag == null)
            return;

        final XmlTag[] subTags = containerTag.findSubTags(rowTagName);
        if(pRow < 0 || pRow >= subTags.length)
            return;
        
        final XmlTag rowTag = subTags[pRow];
        if(rowTag != null)
            setValueInTag(rowTag, pValue, pRow, pColumn);
    }

    protected abstract void setValueInTag(final XmlTag pRowTag,
                                          final Object pValue,
                                          final int pRow,
                                          final int pColumn);

    public final Object getValueAt(final int pRow, final int pColumn) {
        final XmlTag containerTag = findContainerTag();
        if(containerTag == null)
            return null;

        final XmlTag rowTag = containerTag.findSubTags(rowTagName)[pRow];
        return getValueFromTag(rowTag, pRow, pColumn);
    }

    protected abstract Object getValueFromTag(final XmlTag pTag,
                                              final int pRow,
                                              final int pColumn);

    public XmlTag addRow() throws IncorrectOperationException {
        final XmlTag containerTag = findContainerTag(true);
        if(containerTag == null)
            return null;

        final PsiElementFactory factory = PsiManager.getInstance(project).getElementFactory();
        final XmlTag rowTag = factory.createTagFromText("<" + rowTagName + "/>");
        return (XmlTag) containerTag.add(rowTag);
    }

    public void removeRows(final int[] pRowIndices) throws IncorrectOperationException {
        final XmlTag containerTag = findContainerTag(false);
        if(containerTag == null)
            return;

        final XmlTag[] rowTagsToRemove = new XmlTag[pRowIndices.length];
        final XmlTag[] rowTags = containerTag.findSubTags(rowTagName);
        for(int i = 0; i < pRowIndices.length; i++)
            rowTagsToRemove[i] = rowTags[pRowIndices[i]];

        for(XmlTag tagToRemove : rowTagsToRemove)
            tagToRemove.delete();

        final XmlTag[] subTags = containerTag.getSubTags();
        if(subTags.length == 0)
            containerTag.delete();
    }
}
