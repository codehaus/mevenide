/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;

import org.mevenide.core.AbstractGoalsManager;


/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class GoalsManager extends AbstractGoalsManager{
	/** the eclipse plugin */
    private MavenPlugin plugin = MavenPlugin.getPlugin();
    
    /** the preference store used to save/load the runnableGoals */
    private PreferenceStore preferenceStore;
    
    /** list of available goals */
    private File xmlGoals;
   
    /**
	 * @see org.mevenide.core.AbstractGoalsManager#getXmlGoals()
	 */
	protected File getXmlGoals() {
		return xmlGoals;
	}

	/** 
	 * @see org.mevenide.core.AbstractGoalsManager#load()
	 */
	protected void load() throws IOException {
		//@todo IMPLEMENTME
	}

	/** 
	 * @see org.mevenide.core.AbstractGoalsManager#save()
	 */
	public void save() throws IOException {
		//@todo IMPLEMENTME
	}

	/**
	 * @see org.mevenide.core.AbstractGoalsManager#initialize()
	 */
	protected void initialize() {
        if ( plugin == null ) {
            plugin = MavenPlugin.getPlugin();
        }
        String prefs = plugin.getGoalsPreferencesFilename();
        preferenceStore = new PreferenceStore(prefs);
        xmlGoals = new File(plugin.getXmlGoalsFile());
	}

    
}
