package org.mevenide.idea.model.goals.grabber;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface GoalsGrabberListener extends EventListener {

    void goalsGrabberRefreshed(GoalsGrabberRefreshedEvent pEvent);

}
