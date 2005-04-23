package org.mevenide.idea.model.goals.grabber;

import org.mevenide.goals.grabber.IGoalsGrabber;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

/**
 * @author Arik
 */
public class NotifyingGoalsGrabberImpl implements NotifyingGoalsGrabber {

    private final IGoalsGrabber goalsGrabber;

    private final EventListenerList listenerList = new EventListenerList();

    public NotifyingGoalsGrabberImpl(final IGoalsGrabber pGoalsGrabber) {
        goalsGrabber = pGoalsGrabber;
    }

    public void addGoalsGrabberListener(final GoalsGrabberListener pListener) {
        listenerList.add(GoalsGrabberListener.class, pListener);
    }

    public void removeGoalsGrabberListener(final GoalsGrabberListener pListener) {
        listenerList.remove(GoalsGrabberListener.class, pListener);
    }

    public String getDescription(String fullyQualifiedGoalName) {
        return goalsGrabber.getDescription(fullyQualifiedGoalName);
    }

    public String[] getGoals(String plugin) {
        return goalsGrabber.getGoals(plugin);
    }

    public String getName() {
        return goalsGrabber.getName();
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        return goalsGrabber.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPlugins() {
        return goalsGrabber.getPlugins();
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        return goalsGrabber.getPrereqs(fullyQualifiedGoalName);
    }

    public void refresh() throws Exception {
        goalsGrabber.refresh();

        final GoalsGrabberRefreshedEvent event = new GoalsGrabberRefreshedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(GoalsGrabberListener.class);
        for(int i = 0; i < listeners.length; i++) {
            final GoalsGrabberListener listener = (GoalsGrabberListener) listeners[i];
            listener.goalsGrabberRefreshed(event);
        }
    }
}
