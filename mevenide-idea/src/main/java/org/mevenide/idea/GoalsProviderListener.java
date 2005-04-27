package org.mevenide.idea;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface GoalsProviderListener extends EventListener {

    void goalsChanged(GoalsChangedEvent pEvent);
    
}
