package org.mevenide.idea.support.ui;

import org.mevenide.idea.util.Res;

/**
 * @author Arik
 */
public abstract class UIConstants {
    private static final Res RES = Res.getInstance(UIConstants.class);

    private static final String ERROR_TITLE_KEY = "error.title";

    public static final String ERROR_TITLE = RES.get(ERROR_TITLE_KEY);

    private static final String MODULE_SETTINGS_TITLE_KEY = "show.settings.title";
    
    public static final  String MODULE_SETTINGS_TITLE     = RES.get(MODULE_SETTINGS_TITLE_KEY);
}
