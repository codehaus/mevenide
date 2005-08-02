package org.mevenide.idea.project;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface PomManagerListener extends EventListener {
    void pomAdded(PomManagerEvent pEvent);

    void pomRemoved(PomManagerEvent pEvent);

    void pomFileDeleted(PomManagerEvent pEvent);

    void pomFileCreated(PomManagerEvent pEvent);

    void pomFileChanged(PomManagerEvent pEvent);

}
