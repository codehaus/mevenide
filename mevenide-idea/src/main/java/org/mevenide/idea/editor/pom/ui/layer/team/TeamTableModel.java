package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;

/**
 * @author Arik
 */
public class TeamTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(TeamTableModel.class);

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The names of the tags that contain the values for the columns. <p>Note that the last two
     * columns (includes/excludes) are {@code null}, as the values for these columns is calculated
     * manually.</p>
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
     * Creates an instance for the given project and document. The model will use the specified
     * container tag name (e.g. {@code build/resources}) since this model can be used to represent
     * resource lists for more than one location (source code resources, test cases resources,
     * etc).
     *
     * @param pProject          the project this model belongs to
     * @param pDocument         the POM document backing up this model
     * @param pContainerTagName the path to the {@code <resources>} tag, inclusive
     */
    public TeamTableModel(final Project pProject,
                          final Document pDocument,
                          final String pContainerTagName,
                          final String pRowTagName) {
        super(pProject,
              pDocument,
              pContainerTagName,
              pRowTagName,
              TeamTableColumnModel.COLUMN_TITLES,
              VALUE_TAG_NAMES);
    }

    /**
     * Returns {@code true} if the specified column is deemed a patterns column, and needs custom
     * handling not done by the super class.
     *
     * @param pColumn the column index
     * @return boolean
     */
    protected boolean isPatternsColumn(final int pColumn) {
        return valueTagNames[pColumn] == null;
    }

    /**
     * Sets the given patterns in the resource tag. This method works for both the {@code
     * <includes>} and {@code <excludes>} tags as it accepts the container tag name ({@code
     * <includes>} or {@code <excludes>}) and the row tag name ({@code <include>} or {@code
     * <exclude}).
     *
     * <p><b>NOTE</b>: this method replaces current patterns with the new pattern set, not appends
     * it.</p.
     *
     * @param pRowTag              the resource tag - must not be {@code null}
     * @param pPatterns                 the list of patterns
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

    @Override protected void setValueInTag(final XmlTag pRowTag,
                                           final Object pValue,
                                           final int pRow,
                                           final int pColumn) {
        if (!isPatternsColumn(pColumn)) {
            super.setValueInTag(pRowTag, pValue, pRow, pColumn);
            return;
        }

        //
        //find the appropriate resource tag for this row
        //
        final XmlTag resourceTag = findRowTag(pRow);

        final Runnable command = new Runnable() {
            public void run() {
                try {
                    final String[] patterns =
                            pValue == null ?
                                    EMPTY_ARRAY :
                                    (String[]) pValue;
                    setRoles(resourceTag, patterns);
                }
                catch (IncorrectOperationException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };
        IDEUtils.runCommand(project, command);
    }

    @Override protected Object getValueFromTag(final XmlTag pTag,
                                               final int pRow,
                                               final int pColumn) {
        if (!isPatternsColumn(pColumn))
            return super.getValueFromTag(pTag, pRow, pColumn);

        final XmlTag patternContainerTag = pTag.findFirstSubTag("roles");
        if (patternContainerTag == null)
            return null;

        final XmlTag[] patternRowTags = patternContainerTag.findSubTags("role");
        final String[] patterns = new String[patternRowTags.length];
        for (int i = 0; i < patterns.length; i++)
            patterns[i] = patternRowTags[i].getValue().getTrimmedText();

        return patterns;
    }
}
