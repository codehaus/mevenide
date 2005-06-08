package org.mevenide.idea.util.ui.table;

import javax.swing.table.TableModel;

/**
 * @author Arik
 */
public interface CRUDTableModel extends TableModel {

    Object appendRow();

    void removeRows(int... pRowIndices);
}
