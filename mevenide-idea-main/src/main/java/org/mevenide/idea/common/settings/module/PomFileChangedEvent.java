package org.mevenide.idea.common.settings.module;

import java.util.EventObject;
import java.io.File;

/**
 * @author Arik
 */
public class PomFileChangedEvent extends EventObject {
    private final File oldPomFile;
    private final File newPomFile;

    public PomFileChangedEvent(final ModuleSettingsModel source,
                               final File pNewPomFile,
                               final File pOldPomFile) {
        super(source);
        newPomFile = pNewPomFile;
        oldPomFile = pOldPomFile;
    }

    public ModuleSettingsModel getModuleSettings() {
        return (ModuleSettingsModel) source;
    }

    public File getNewPomFile() {
        return newPomFile;
    }

    public File getOldPomFile() {
        return oldPomFile;
    }
}