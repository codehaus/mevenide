package org.mevenide.idea.util.event;

/**
 * @author Arik
 */
public class BeanRowRemovedEvent extends BeanRowEvent {
    public BeanRowRemovedEvent(final BeanRowsObservable pSource, final int pRow) {
        super(pSource, EventType.ROW_REMOVED, pRow, null);
    }
}
