package org.mevenide.idea.util.goals.grabber;

import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.idea.*;
import org.mevenide.idea.IDEALocationFinder;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

/**
 * A goals grabber that implements the {@link GoalsProvider} interface. It only
 * returns the global goals defined by Maven and/or plugins installed by the
 * user, and does not include any module-specific goals (such as in maven.xml
 * scripts).
 *
 * @author Arik
 */
public class GlobalGoalsGrabber extends DefaultGoalsGrabber implements GoalsProvider {
    /**
     * The listener management support, for notifying when refreshed.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Creates an instance.
     *
     * @throws Exception if errors occur
     */
    public GlobalGoalsGrabber() throws Exception {
        super(new IDEALocationFinder());
    }

    public void refreshGoals() throws GoalGrabbingException {
        try {
            refresh();
            fireGoalsChangedEvent();
        }
        catch (Exception e) {
            if(e instanceof GoalGrabbingException)
                throw (GoalGrabbingException)e;
            else
                throw new GoalGrabbingException(e);
        }
    }

    public void addGoalsProviderListener(final GoalsProviderListener pListener) {
        listenerList.add(GoalsProviderListener.class, pListener);
    }

    public void removeGoalsProviderListener(final GoalsProviderListener pListener) {
        listenerList.remove(GoalsProviderListener.class, pListener);
    }

    /**
     * Fires the goals changed event to registered listeners.
     */
    protected void fireGoalsChangedEvent() {
        final GoalsChangedEvent event = new GoalsChangedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(GoalsProviderListener.class);
        for (int i = 0; i < listeners.length; i++) {
            final GoalsProviderListener listener = (GoalsProviderListener) listeners[i];
            listener.goalsChanged(event);
        }
    }
}
