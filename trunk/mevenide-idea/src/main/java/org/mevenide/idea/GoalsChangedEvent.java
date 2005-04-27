package org.mevenide.idea;

import java.util.EventObject;

/**
 * @author Arik
 */
public class GoalsChangedEvent extends EventObject {

    public GoalsChangedEvent(final GoalsProvider pProvider) {
        super(pProvider);
    }

    public GoalsProvider getGoalsProvider() {
        return (GoalsProvider) source;
    }
}
