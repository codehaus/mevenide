package org.mevenide.idea.util.event;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface BeanRowsListener extends EventListener {
    void rowAdded(BeanRowEvent pEvent);

    void rowRemoved(BeanRowEvent pEvent);

    void rowChanged(BeanRowEvent pEvent);

    void rowsChanged(BeanRowEvent pEvent);

}
