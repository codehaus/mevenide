package org.mevenide.idea.util.ui.table;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;

import javax.swing.table.TableModel;

/**
 * @author Arik
 */
public interface MutableXmlPsiTableModel extends TableModel {
    XmlTag addRow() throws IncorrectOperationException;
    void removeRows(final int[] pRowIndices) throws IncorrectOperationException;
}
