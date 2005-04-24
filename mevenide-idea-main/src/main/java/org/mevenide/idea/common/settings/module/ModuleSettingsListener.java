package org.mevenide.idea.common.settings.module;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface ModuleSettingsListener extends EventListener {
    void pomFileChanged(PomFileChangedEvent pEvent);
    void moduleGoalsChanged(ModuleGoalsChangedEvent pEvent);
}
