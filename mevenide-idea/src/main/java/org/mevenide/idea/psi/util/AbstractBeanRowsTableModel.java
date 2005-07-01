package org.mevenide.idea.psi.util;

import javax.swing.table.AbstractTableModel;
import org.mevenide.idea.util.ui.table.CRUDTableModel;
import org.mevenide.idea.util.event.BeanRowsListener;
import org.mevenide.idea.util.event.BeanRowsObservable;
import org.mevenide.idea.util.event.BeanRowEvent;

/**
 * @author Arik
 */
public abstract class AbstractBeanRowsTableModel<PsiModel extends BeanRowsObservable> extends AbstractTableModel implements CRUDTableModel, BeanRowsListener {

    protected final PsiModel model;
    protected final String[] columnTitles;

    protected AbstractBeanRowsTableModel(final PsiModel pModel) {
        this(pModel, null);
    }

    protected AbstractBeanRowsTableModel(final PsiModel pModel,
                                         final String[] pColumnTitles) {
        model = pModel;
        model.addBeanRowsListener(this);
        columnTitles = pColumnTitles;
    }

    @Override
    public String getColumnName(int column) {
        if(columnTitles == null || column >= columnTitles.length)
            return super.getColumnName(column);
        else
            return columnTitles[column];
    }

    public int getColumnCount() {
        if (columnTitles == null)
            return 0;
        else
            return columnTitles.length;
    }

    public final PsiModel getModel() {
        return model;
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public Object appendRow() {
        return model.appendRow();
    }

    public void removeRows(final int... pRowIndices) {
        model.deleteRows(pRowIndices);
    }

    public void rowAdded(final BeanRowEvent pEvent) {
        fireTableRowsInserted(pEvent.getRow(), pEvent.getRow());
    }

    public void rowRemoved(final BeanRowEvent pEvent) {
        fireTableRowsDeleted(pEvent.getRow(), pEvent.getRow());
    }

    public void rowsChanged(final BeanRowEvent pEvent) {
        fireTableStructureChanged();
    }

    public void rowChanged(final BeanRowEvent pEvent) {
        final int changedColumn = getColumnIndexByProperty(pEvent.getField());
        if(changedColumn < 0)
            fireTableRowsUpdated(pEvent.getRow(), pEvent.getRow());
        else
            fireTableCellUpdated(pEvent.getRow(), changedColumn);
    }

    protected abstract int getColumnIndexByProperty(final String pProperyName);
}
