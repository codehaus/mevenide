package org.mevenide.idea.settings.module;

import org.mevenide.idea.settings.module.ModuleSettings;

import java.io.File;
import java.util.EventObject;

/**
 * @author Arik
 */
public class PomFileChangedEvent extends EventObject {
    private final File oldPomFile;
    private final File newPomFile;

    public PomFileChangedEvent(final ModuleSettings source,
                               final File pNewPomFile,
                               final File pOldPomFile) {
        super(source);
        newPomFile = pNewPomFile;
        oldPomFile = pOldPomFile;
    }

    public ModuleSettings getModuleSettings() {
        return (ModuleSettings) source;
    }

    public File getNewPomFile() {
        return newPomFile;
    }

    public File getOldPomFile() {
        return oldPomFile;
    }
}
