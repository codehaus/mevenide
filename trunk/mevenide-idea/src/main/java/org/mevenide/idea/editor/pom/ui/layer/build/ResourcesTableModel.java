package org.mevenide.idea.editor.pom.ui.layer.build;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.psi.MultiValuedXmlTagRowsTableModel;

/**
 * The table model for resources table.
 *
 * <p>This class extends the standard {@link MultiValuedXmlTagRowsTableModel simple tag-based} model
 * to override the mechanism for extracting the values for the {@code <includes>} and {@code
 * <excludes>} columns.</p>
 *
 * <p>The includes/excludes columns are not editable directly (the text field is not editable) but
 * have a small "Browse" button which pops up a dialog with the {@link org.mevenide.idea.util.ui.StringListEditPanel} which
 * allow the actual editing. When the dialog is closed (with the OK button), the selected patterns
 * are pushed back into the text field. Once the user exits the text field, the values are pushed
 * back into this model.</p>
 *
 * <p>This is required since the includes/excludes fields are not simple valued - they contain
 * themselves a list of {@code <include>} or {@code <exclude>} tags and therefor we need to manually
 * extract these into a string array.</p>
 *
 * @author Arik
 */
public class ResourcesTableModel extends MultiValuedXmlTagRowsTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ResourcesTableModel.class);

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The names of the tags that contain lists of patterns. Each
     * such tag will contain multiple instances of one of the tags
     * in {@link #PATTERN_TAG_NAMES}.
     */
    private static final String[] PATTERNS_CONTAINER_TAG_NAMES = new String[] {
        "includes",
        "excludes"
    };

    /**
     * The names of the tags that contain the actual patterns. Each such
     * tag appears one or more times under one of the {@link #PATTERNS_CONTAINER_TAG_NAMES}
     * tags.
     */
    private static final String[] PATTERN_TAG_NAMES = new String[] {
        "include",
        "exclude"
    };

    /**
     * The names of the tags that contain the values for the columns. <p>Note that the last two
     * columns (includes/excludes) are {@code null}, as the values for these columns is calculated
     * manually.</p>
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "directory",
        "targetPath",
        null,
        null
    };

    /**
     * The default row tag name.
     */
    private static final String ROW_TAG_NAME = "resource";

    /**
     * Creates an instance for the given project and document. The model will use the specified
     * container tag name (e.g. {@code build/resources}) since this model can be used to represent
     * resource lists for more than one location (source code resources, test cases build,
     * etc).
     *
     * @param pFile the POM file
     * @param pContainerTagName the path to the {@code <resources>} tag, inclusive
     */
    public ResourcesTableModel(final XmlFile pFile,
                               final String pContainerTagName) {
        super(pFile, pContainerTagName, ROW_TAG_NAME, VALUE_TAG_NAMES);
    }

    /**
     * Returns {@code true} if the specified column is deemed a patterns column, and needs custom
     * handling not done by the super class.
     *
     * @param pColumn the column index
     * @return boolean
     */
    protected boolean isPatternsColumn(final int pColumn) {
        return VALUE_TAG_NAMES[pColumn] == null;
    }

    /**
     * Sets the given patterns in the resource tag. This method works for
     * both the {@code <includes>} and {@code <excludes>} tags as it
     * accepts the container tag name ({@code <includes>} or {@code <excludes>})
     * and the row tag name ({@code <include>} or {@code <exclude}).
     *
     * <p><b>NOTE</b>: this method replaces current patterns with the new pattern set,
     * not appends it.</p.
     *
     * @param pResourceTag the resource tag - must not be {@code null}
     * @param pPatternsContainerTagName the container tag name for the patterns
     * @param pPatternRowTagName the row tag name for each pattern
     * @param pPatterns the list of patterns
     */
    protected void setResourcePatterns(final XmlTag pResourceTag,
                                       final String pPatternsContainerTagName,
                                       final String pPatternRowTagName,
                                       final String[] pPatterns)
                                                                        throws IncorrectOperationException {
        XmlTag patternsContainerTag = pResourceTag.findFirstSubTag(pPatternsContainerTagName);
        if (patternsContainerTag == null) {
            patternsContainerTag = pResourceTag.createChildTag(
                    pPatternsContainerTagName,
                    pResourceTag.getNamespace(),
                    null,
                    false);
            patternsContainerTag = (XmlTag) pResourceTag.add(patternsContainerTag);
        }
        else {
            final XmlTag[] children = patternsContainerTag.findSubTags(pPatternRowTagName);
            for (XmlTag child : children)
                child.delete();
        }

        for (String pattern : pPatterns) {
            final XmlTag rowTag = patternsContainerTag.createChildTag(
                    pPatternRowTagName,
                    patternsContainerTag.getNamespace(),
                    pattern,
                    false);
            patternsContainerTag.add(rowTag);
        }
    }

    protected Object getTagRowValue(final XmlTag pTag, final int pColumn) {
        if (!isPatternsColumn(pColumn))
            return super.getTagRowValue(pTag, pColumn);

        //
        //find out the appropriate tag names based on the column index
        //we add 2 to the index, since the tag names arrays start with 0,
        //but the columns hav two extra columns before the pattern columns
        //
        final XmlTag patternContainerTag = pTag.findFirstSubTag(PATTERNS_CONTAINER_TAG_NAMES[pColumn - 2]);
        if (patternContainerTag == null)
            return null;

        final XmlTag[] patternRowTags = patternContainerTag.findSubTags(PATTERN_TAG_NAMES[pColumn - 2]);
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

            //
            //find out the appropriate tag names based on the column index
            //we add 2 to the index, since the tag names arrays start with 0,
            //but the columns hav two extra columns before the pattern columns
            //
            setResourcePatterns(pTag,
                                PATTERNS_CONTAINER_TAG_NAMES[pColumn - 2],
                                PATTERN_TAG_NAMES[pColumn - 2],
                                patterns);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
