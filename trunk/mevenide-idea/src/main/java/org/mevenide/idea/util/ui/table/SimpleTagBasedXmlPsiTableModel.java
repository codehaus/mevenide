package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlTag;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.psi.PsiUtils;

/**
 * @author Arik
 */
public class SimpleTagBasedXmlPsiTableModel extends TagBasedXmlPsiTableModel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SimpleTagBasedXmlPsiTableModel.class);

    private final String[] columnTitles;
    private final String[] valueTagNames;

    public SimpleTagBasedXmlPsiTableModel(final Project pProject,
                                          final Document pIdeaDocument,
                                          final String pContainerTagName,
                                          final String pRowTagName,
                                          final String[] pColumnTitles,
                                          final String[] pValueTagNames) {
        super(pProject, pIdeaDocument, pContainerTagName, pRowTagName);
        columnTitles = pColumnTitles;
        valueTagNames = pValueTagNames;
        refreshModel();
    }

    /**
     * Returns the type of the specified column.
     *
     * <p>By default, this implementation always return {@code String.class}, but you can override
     * and return other classes instead.</p>
     *
     * @param pColumn the column to get the class for
     * @return class
     */
    @Override public Class<?> getColumnClass(final int pColumn) {
        return String.class;
    }

    @Override public String getColumnName(final int pColumn) {
        if (pColumn < 0 || pColumn > getColumnCount())
            throw new IllegalArgumentException(RES.get("illegal.column.index", pColumn));

        return columnTitles[pColumn];
    }

    /**
     * By default, this implementation returns {@code true}. Override to customize it.
     *
     * @param pRow    the queried row
     * @param pColumn the queried column
     * @return boolean
     */
    @Override public boolean isCellEditable(final int pRow,
                                            final int pColumn) {
        return true;
    }

    public int getColumnCount() {
        return columnTitles.length;
    }

    protected Object getValueFromTag(final XmlTag pTag,
                                     final int pRow,
                                     final int pColumn) {
        final String valueTagName = valueTagNames[pColumn];
        final XmlTag valueTag = pTag.findFirstSubTag(valueTagName);
        if (valueTag == null)
            return null;

        return valueTag.getValue().getTrimmedText();
    }

    protected void setValueInTag(final XmlTag pRowTag,
                                 final Object pValue,
                                 final int pRow,
                                 final int pColumn) {
        final String stringValue =
                pValue == null ? null : pValue.toString();

        final String valueTagName = valueTagNames[pColumn];

        PsiUtils.setTagValue(project, pRowTag, valueTagName, stringValue);
    }

    @Override protected void refreshModel(final PsiEventType pEventType,
                                          final PsiTreeChangeEvent pEvent) {
        fireTableStructureChanged();
    }
}
