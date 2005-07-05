package org.mevenide.idea.util.event;

import java.util.EventObject;

/**
 * @author Arik
 */
public abstract class BeanRowEvent extends EventObject {
    public static enum EventType {
        ROW_ADDED,
        ROW_REMOVED,
        ROW_CHANGED,
        ROWS_CHANGED
    }

    private final EventType type;
    private final int row;
    private final String field;

    public BeanRowEvent(final BeanRowsObservable pSource,
                        final EventType pType,
                        final int pRow,
                        final String pField) {
        super(pSource);
        type = pType;
        row = pRow;
        field = pField;
    }

    @Override
    public BeanRowsObservable getSource() {
        return (BeanRowsObservable) super.getSource();
    }

    public final EventType getType() {
        return type;
    }

    public final int getRow() {
        return row;
    }

    public String getField() {
        return field;
    }
}
