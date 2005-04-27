package org.mevenide.idea.util.goals.grabber.notify;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface GoalsGrabberListener extends EventListener {

    void goalsGrabberRefreshed(GoalsGrabberRefreshedEvent pEvent);

}
