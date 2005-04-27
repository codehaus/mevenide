package org.mevenide.idea.global;

import java.util.EventListener;

/**
 * An event listener for the Maven manager component.
 *
 * @author Arik
 */
public interface MavenManagerListener extends EventListener {

    /**
     * Invoked when the user changes the Maven home.
     *
     * @param pEvent the event information object
     */
    void mavenHomeChanged(MavenHomeChangedEvent pEvent);

}
