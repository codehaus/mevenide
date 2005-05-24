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
public abstract class TagBasedXmlPsiTableModel extends AbstractXmlPsiTableModel {
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
    protected final String[] containerTagPath;

    /**
     * The name of the tag which represents a single row.
     */
    protected final String rowTagName;

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
     * @param pContainerTagName the name of the container tag
     * @param pRowTagName the name of the row tags
     */
    public TagBasedXmlPsiTableModel(final Project pProject,
                                    final Document pIdeaDocument,
                                    final String pContainerTagName,
                                    final String pRowTagName) {
        super(pProject, pIdeaDocument);
        containerTagPath = pContainerTagName.split("/");
        rowTagName = pRowTagName;
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
    protected XmlTag findContainerTag(final boolean pCreateIfNotFound)
            throws IncorrectOperationException {

        final XmlDocument xmlDocument = xmlFile.getDocument();
        if (xmlDocument == null)
            return null;

        final XmlTag projectTag = xmlDocument.getRootTag();
        if (projectTag == null)
            return null;

        final PsiManager mgr = PsiManager.getInstance(project);
        final PsiElementFactory factory = mgr.getElementFactory();
        XmlTag tag = projectTag;
        for (final String tagName : containerTagPath) {
            XmlTag currentTag = tag.findFirstSubTag(tagName);
            if (currentTag == null) {
                if(!pCreateIfNotFound)
                    return null;

                currentTag = factory.createTagFromText("<" + tagName + "/>");
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
    protected final XmlTag findContainerTag() {
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
        final XmlTag rowTag = containerTag.findSubTags(rowTagName)[pRow];

        setValueInTag(rowTag, pValue, pRow, pColumn);
    }

    protected abstract void setValueInTag(final XmlTag pRowTag,
                                          final Object pValue,
                                          final int pRow,
                                          final int pColumn);

    public final Object getValueAt(final int pRow, final int pColumn) {
        final XmlTag containerTag = findContainerTag();
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

        final String text = containerTag.getValue().getTrimmedText();
        if(text == null || text.trim().length() == 0)
            containerTag.delete();
    }
}
