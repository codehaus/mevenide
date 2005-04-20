package org.mevenide.idea.common.settings.module;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface ModuleSettingsListener extends EventListener {
    void pomFileChanged(PomFileChangedEvent pEvent);
    void favoriteGoalsChanged(FavoriteGoalsChangedEvent pEvent);
}
