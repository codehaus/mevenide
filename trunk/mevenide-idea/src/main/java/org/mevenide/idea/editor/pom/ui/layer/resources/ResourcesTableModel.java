package org.mevenide.idea.editor.pom.ui.layer.resources;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;
import org.mevenide.idea.util.IDEUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The table model for resources table.
 *
 * <p>This class extends the standard {@link SimpleTagBasedXmlPsiTableModel simple tag-based} model
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
public class ResourcesTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ResourcesTableModel.class);

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The titles for the model columns.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Directory",
        "Target Path",
        "Includes",
        "Excludes"
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
     * resource lists for more than one location (source code resources, test cases resources,
     * etc).
     *
     * @param pProject          the project this model belongs to
     * @param pDocument         the POM document backing up this model
     * @param pContainerTagName the path to the {@code <resources>} tag, inclusive
     */
    public ResourcesTableModel(final Project pProject,
                               final Document pDocument,
                               final String pContainerTagName) {
        super(pProject,
              pDocument,
              pContainerTagName,
              ROW_TAG_NAME,
              COLUMN_TITLES,
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

        //
        //determine if we are setting the includes or excludes patterns
        //
        final String containerTagName;
        final String rowTagName;
        if (pColumn == 2) {
            containerTagName = "includes";
            rowTagName = "include";
        }
        else {
            containerTagName = "excludes";
            rowTagName = "exclude";
        }

        final Runnable command = new Runnable() {
            public void run() {
                try {
                    final String[] patterns =
                            pValue == null ?
                                    EMPTY_ARRAY :
                                    (String[]) pValue;
                    setResourcePatterns(resourceTag, containerTagName, rowTagName, patterns);
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

        final String patternContainerTagName;
        final String patternRowTagName;
        if (pColumn == 2) {
            patternContainerTagName = "includes";
            patternRowTagName = "include";
        }
        else {
            patternContainerTagName = "excludes";
            patternRowTagName = "exclude";
        }

        final XmlTag patternContainerTag = pTag.findFirstSubTag(patternContainerTagName);
        if (patternContainerTag == null)
            return null;

        final XmlTag[] patternRowTags = patternContainerTag.findSubTags(patternRowTagName);
        final String[] patterns = new String[patternRowTags.length];
        for (int i = 0; i < patterns.length; i++)
            patterns[i] = patternRowTags[i].getValue().getTrimmedText();

        return patterns;
    }
}
