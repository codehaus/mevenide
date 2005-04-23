package org.mevenide.idea.model.goals.grabber;

import org.mevenide.goals.grabber.IGoalsGrabber;

/**
 * @author Arik
 */
public interface NotifyingGoalsGrabber extends IGoalsGrabber {
    void addGoalsGrabberListener(GoalsGrabberListener pListener);

    void removeGoalsGrabberListener(GoalsGrabberListener pListener);
}
