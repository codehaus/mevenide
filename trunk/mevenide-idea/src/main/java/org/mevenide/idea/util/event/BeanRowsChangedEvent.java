package org.mevenide.idea.util.event;

/**
 * @author Arik
 */
public class BeanRowsChangedEvent extends BeanRowEvent {
    public BeanRowsChangedEvent(final BeanRowsObservable pSource) {
        super(pSource, EventType.ROWS_CHANGED, -1, null);
    }
}
