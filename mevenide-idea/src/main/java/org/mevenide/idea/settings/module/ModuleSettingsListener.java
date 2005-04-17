package org.mevenide.idea.settings.module;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface ModuleSettingsListener extends EventListener {

    void pomFileChanged(PomFileChangedEvent pEvent);

    void favoriteGoalsChanged(FavoriteGoalsChangedEvent pEvent);
}
