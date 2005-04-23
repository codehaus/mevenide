package org.mevenide.idea.model.goals.grabber;

import org.mevenide.goals.grabber.IGoalsGrabber;

import java.util.EventObject;

/**
 * @author Arik
 */
public class GoalsGrabberRefreshedEvent extends EventObject {

    public GoalsGrabberRefreshedEvent(final IGoalsGrabber pSource) {
        super(pSource);
    }

    public IGoalsGrabber getGoalsGrabber() {
        return (IGoalsGrabber) source;
    }
}
