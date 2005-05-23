package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;

/**
 * @author Arik
 */
public abstract class TagBasedXmlPsiTableModel extends AbstractXmlPsiTableModel {
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

    protected XmlTag findContainerTag() {
        final XmlDocument xmlDocument = xmlFile.getDocument();
        if (xmlDocument == null)
            return null;

        final XmlTag projectTag = xmlDocument.getRootTag();
        if (projectTag == null)
            return null;

        XmlTag tag = projectTag;
        for (int i = 0; i < containerTagPath.length && tag != null; i++) {
            final String tagName = containerTagPath[i];
            tag = tag.findFirstSubTag(tagName);
        }

        return tag;
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

}
