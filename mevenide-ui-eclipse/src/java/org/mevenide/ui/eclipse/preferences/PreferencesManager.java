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
 */
package org.mevenide.ui.eclipse.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;

import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PreferencesManager {
	private PreferenceStore preferenceStore;
	
	public void loadPreferences() {
		preferenceStore = new PreferenceStore(getPreferenceStoreFilename());
		try {
			preferenceStore.load();
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	public boolean store() {
		try {
			preferenceStore.save();		
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String getPreferenceStoreFilename() {
		return MavenPlugin.getPlugin().getPreferencesFilename() ; 
	}
	
	public String getValue(String property) {
		return preferenceStore.getString(property);
	}
	
	public void setValue(String property, String value) {
		preferenceStore.setValue(property, value);
	}
}
