package org.mevenide.idea.settings.global;

import org.mevenide.idea.settings.global.GlobalSettings;

import java.util.EventObject;
import java.io.File;

/**
 * @author Arik
 */
public class MavenHomeChangedEvent extends EventObject {

    private final File oldMavenHome;
    private final File newMavenHome;

    public MavenHomeChangedEvent(final GlobalSettings pSource,
                                 final File pOldMavenHome,
                                 final File pNewMavenHome) {
        super(pSource);
        oldMavenHome = pOldMavenHome;
        newMavenHome = pNewMavenHome;
    }

    public GlobalSettings getGlobalSettings() {
        return (GlobalSettings) source;
    }

    public File getOldMavenHome() {
        return oldMavenHome;
    }

    public File getNewMavenHome() {
        return newMavenHome;
    }
}
