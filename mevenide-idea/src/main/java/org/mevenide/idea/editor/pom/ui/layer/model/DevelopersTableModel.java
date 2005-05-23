package org.mevenide.idea.editor.pom.ui.layer.model;

import org.mevenide.idea.util.ui.table.TagBasedXmlPsiTableModel;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.Res;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.PsiTreeChangeEvent;

/**
 * @author Arik
 * @todo add support to roles
 */
public class DevelopersTableModel extends TagBasedXmlPsiTableModel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DevelopersTableModel.class);

    /**
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Name",
        "ID",
        "E-Mail",
        "Organization",
        "URL",
        "Timezone"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] COLUMN_PROPERTY_NAMES = new String[]{
        "name",
        "id",
        "email",
        "organization",
        "url",
        "timezone"
    };

    /**
     * Number of columns this model provides.
     */
    private static final int COLUMN_COUNT = COLUMN_TITLES.length;

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public DevelopersTableModel(final Project pProject,
                                final Document pIdeaDocument) {
        this(pProject, pIdeaDocument, "developers", "developer");
    }

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject          the project.
     * @param pIdeaDocument     the document.
     * @param pContainerTagName the container tag name
     * @param pRowTagName       the row tag name
     */
    public DevelopersTableModel(final Project pProject,
                                final Document pIdeaDocument,
                                final String pContainerTagName,
                                final String pRowTagName) {
        super(pProject, pIdeaDocument, pContainerTagName, pRowTagName);
        refreshModel();
    }

    @Override public Class<?> getColumnClass(final int pColumn) {
        return String.class;
    }

    @Override public String getColumnName(final int pColumn) {
        if (pColumn < 0 || pColumn > COLUMN_COUNT)
            throw new IllegalArgumentException(RES.get("illegal.column.index", pColumn));

        return COLUMN_TITLES[pColumn];
    }

    @Override public boolean isCellEditable(final int pRow, final int pColumn) {
        return true;
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    protected Object getValueFromTag(final XmlTag pTag,
                                     final int pRow,
                                     final int pColumn) {
        final String valueTagName = COLUMN_PROPERTY_NAMES[pColumn];
        final XmlTag valueTag = pTag.findFirstSubTag(valueTagName);
        if (valueTag == null)
            return null;

        return valueTag.getValue().getTrimmedText();
    }

    protected void setValueInTag(final XmlTag pRowTag,
                                 final Object pValue,
                                 final int pRow,
                                 final int pColumn) {
        final String stringValue = pValue == null ? null : pValue.toString();
        final String valueTagName = COLUMN_PROPERTY_NAMES[pColumn];

        PsiUtils.setTagValue(project, pRowTag, valueTagName, stringValue);
    }

    @Override protected void refreshModel(final PsiEventType pEventType,
                                          final PsiTreeChangeEvent pEvent) {
        fireTableStructureChanged();
    }
}
