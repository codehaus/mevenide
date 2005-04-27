package org.mevenide.idea.util.goals.grabber.notify;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.goals.grabber.notify.GoalsGrabberListener;

/**
 * @author Arik
 */
public interface NotifyingGoalsGrabber extends IGoalsGrabber {
    void addGoalsGrabberListener(GoalsGrabberListener pListener);

    void removeGoalsGrabberListener(GoalsGrabberListener pListener);
}
