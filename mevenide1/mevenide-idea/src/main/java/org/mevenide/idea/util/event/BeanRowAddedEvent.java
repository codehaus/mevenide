package org.mevenide.idea.util.event;

/**
 * @author Arik
 */
public class BeanRowAddedEvent extends BeanRowEvent {
    public BeanRowAddedEvent(final BeanRowsObservable pSource,
                             final int pRow) {
        super(pSource, EventType.ROW_ADDED, pRow, null);
    }
}
