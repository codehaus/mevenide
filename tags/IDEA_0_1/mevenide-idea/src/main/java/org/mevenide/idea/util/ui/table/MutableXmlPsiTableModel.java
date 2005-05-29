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

    String[] getContainerTagPath();

    void setContainerTagPath(String pContainerTagPath);

    void setContainerTagPath(String[] pContainerTagPath);

    String getRowTagName();

    void setRowTagName(String pRowTagName);

    void setTagPath(String pContainerTagPath, String pRowTagName);

    void setTagPath(String[] pContainerTagPath, String pRowTagName);

    XmlTag findContainerTag(boolean pCreateIfNotFound)
            throws IncorrectOperationException;

    XmlTag findContainerTag();
}
