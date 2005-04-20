package org.mevenide.idea.common.settings.global;

import org.mevenide.idea.common.settings.global.GlobalSettingsModel;

import java.util.EventObject;
import java.io.File;

/**
 * @author Arik
 */
public class MavenHomeChangedEvent extends EventObject {
    private final File oldMavenHome;
    private final File newMavenHome;

    public MavenHomeChangedEvent(final GlobalSettingsModel pSource,
                                 final File pOldMavenHome,
                                 final File pNewMavenHome) {
        super(pSource);
        oldMavenHome = pOldMavenHome;
        newMavenHome = pNewMavenHome;
    }

    public GlobalSettingsModel getGlobalSettings() {
        return (GlobalSettingsModel) source;
    }

    public File getOldMavenHome() {
        return oldMavenHome;
    }

    public File getNewMavenHome() {
        return newMavenHome;
    }
}