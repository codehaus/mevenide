package org.mevenide.idea.util.event;

/**
 * @author Arik
 */
public interface BeanRowsObservable {
    void addBeanRowsListener(BeanRowsListener pListener);

    void removeBeanRowsListener(BeanRowsListener pListener);

    int getRowCount();

    int appendRow();

    void deleteRows(int... pRowIndices);
}
