package org.mevenide.idea.project;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface PomManagerListener extends EventListener {
    void pomAdded(PomManagerEvent pEvent);

    void pomRemoved(PomManagerEvent pEvent);

    void pomValidityChanged(PomManagerEvent pEvent);

    void pomGoalsChanged(PomManagerEvent pEvent);

    void pomJdkChanged(PomManagerEvent pEvent);
}
