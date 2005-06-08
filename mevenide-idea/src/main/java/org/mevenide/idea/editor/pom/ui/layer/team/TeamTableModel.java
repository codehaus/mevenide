package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.psi.MultiValuedXmlTagRowsTableModel;

/**
 * @author Arik
 */
public class TeamTableModel extends MultiValuedXmlTagRowsTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(TeamTableModel.class);

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The names of the tags that contain the values for the columns. <p>Note that the last two columns
     * (includes/excludes) are {@code null}, as the values for these columns is calculated manually.</p>
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "name",
        "id",
        "email",
        "organization",
        "url",
        "timezone",
        null
    };

    /**
     * Creates an instance for the given project and document. The model will use the specified container tag name (e.g.
     * {@code build/resources}) since this model can be used to represent resource lists for more than one location
     * (source code resources, test cases build, etc).
     *
     * @param pFile the POM file
     * @param pContainerTagName the path to the {@code <resources>} tag, inclusive
     */
    public TeamTableModel(final XmlFile pFile,
                          final String pContainerTagName,
                          final String pRowTagName) {
        super(pFile,
              pContainerTagName,
              pRowTagName,
              VALUE_TAG_NAMES);
    }

    /**
     * Returns {@code true} if the specified column is deemed a patterns column, and needs custom handling not done by
     * the super class.
     *
     * @param pColumn the column index
     * @return boolean
     */
    protected boolean isPatternsColumn(final int pColumn) {
        return VALUE_TAG_NAMES[pColumn] == null;
    }

    /**
     * Sets the given patterns in the resource tag. This method works for both the {@code <includes>} and {@code
     * <excludes>} tags as it accepts the container tag name ({@code <includes>} or {@code <excludes>}) and the row tag
     * name ({@code <include>} or {@code <exclude}).
     *
     * <p><b>NOTE</b>: this method replaces current patterns with the new pattern set, not appends it.</p.
     *
     * @param pRowTag   the resource tag - must not be {@code null}
     * @param pPatterns the list of patterns
     */
    protected void setRoles(final XmlTag pRowTag,
                            final String[] pPatterns)
        throws IncorrectOperationException {
        XmlTag patternsContainerTag = pRowTag.findFirstSubTag("roles");
        if (patternsContainerTag == null) {
            patternsContainerTag = pRowTag.createChildTag(
                "roles",
                pRowTag.getNamespace(),
                null,
                false);
            patternsContainerTag = (XmlTag) pRowTag.add(patternsContainerTag);
        }
        else {
            final XmlTag[] children = patternsContainerTag.findSubTags("role");
            for (XmlTag child : children)
                child.delete();
        }

        for (String pattern : pPatterns) {
            final XmlTag rowTag = patternsContainerTag.createChildTag(
                "role",
                patternsContainerTag.getNamespace(),
                pattern,
                false);
            patternsContainerTag.add(rowTag);
        }
    }

    @Override
    protected Object getTagRowValue(final XmlTag pTag, final int pColumn) {
        if (!isPatternsColumn(pColumn))
            return super.getTagRowValue(pTag, pColumn);

        final XmlTag patternContainerTag = pTag.findFirstSubTag("roles");
        if (patternContainerTag == null)
            return null;

        final XmlTag[] patternRowTags = patternContainerTag.findSubTags("role");
        final String[] patterns = new String[patternRowTags.length];
        for (int i = 0; i < patterns.length; i++)
            patterns[i] = patternRowTags[i].getValue().getTrimmedText();

        return patterns;
    }

    @Override
    protected void setTagRowValue(final XmlTag pTag, final Object pValue, final int pColumn) {
        if (!isPatternsColumn(pColumn)) {
            super.setTagRowValue(pTag, pValue, pColumn);
            return;
        }

        try {
            final String[] patterns =
                pValue == null ? EMPTY_ARRAY : (String[]) pValue;
            setRoles(pTag, patterns);
        }

        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
