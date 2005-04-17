package org.mevenide.idea.settings.module;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.EventObject;

/**
 * @author Arik
 */
public class FavoriteGoalsChangedEvent extends EventObject {

    private final Collection oldFavorites;
    private final Collection newFavorites;

    public FavoriteGoalsChangedEvent(final ModuleSettings pSource,
                                     final Collection pNewFavorites,
                                     final Collection pOldFavorites) {
        super(pSource);
        newFavorites = pNewFavorites;
        oldFavorites = pOldFavorites;
    }

    public ModuleSettings getModuleSettings() {
        return (ModuleSettings) source;
    }
    
    public Collection getNewFavorites() {
        return newFavorites;
    }

    public Collection getOldFavorites() {
        return oldFavorites;
    }

    public Collection getAddedFavorites() {
        return CollectionUtils.subtract(newFavorites, oldFavorites);
    }

    public Collection getRemovedFavorites() {
        return CollectionUtils.subtract(oldFavorites, newFavorites);
    }
}
