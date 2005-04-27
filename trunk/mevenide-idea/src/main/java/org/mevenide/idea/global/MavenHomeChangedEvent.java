package org.mevenide.idea.global;

import java.util.EventObject;

/**
 * @author Arik
 */
public class MavenHomeChangedEvent extends EventObject {

    public MavenHomeChangedEvent(final MavenManager pMavenManager) {
        super(pMavenManager);
    }

    public MavenManager getMavenManager() {
        return (MavenManager) source;
    }
}
