package org.mevenide.idea.util.event;

/**
 * @author Arik
 */
public class BeanRowChangedEvent extends BeanRowEvent {
    private final Object value;

    public BeanRowChangedEvent(final BeanRowsObservable pSource,
                               final int pRow,
                               final String pField,
                               final Object pValue) {
        super(pSource, EventType.ROW_CHANGED, pRow, pField);
        value = pValue;
    }

    public final Object getValue() {
        return value;
    }
}
