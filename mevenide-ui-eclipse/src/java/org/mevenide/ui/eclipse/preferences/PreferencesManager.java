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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.PreferenceStore;

import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PreferencesManager {
	private static Log log = LogFactory.getLog(PreferencesManager.class);
	
	private PreferenceStore preferenceStore;
	
	public void loadPreferences() {
		preferenceStore = new PreferenceStore(getPreferenceStoreFilename());
		try {
			preferenceStore.load();
		}
		catch ( Exception ex ) {
			log.debug("Unable to load preferences from file '" + getPreferenceStoreFilename() + "' due to : " + ex);
		}
	}
	
	public boolean store() {
		try {
			preferenceStore.save();		
			return true;
		}
		catch (IOException e) {
			log.debug("Unable to save preferences to file '" + getPreferenceStoreFilename() + "' due to : " + e);
			return false;
		}
	}
	
	private String getPreferenceStoreFilename() {
		return Mevenide.getPlugin().getPreferencesFilename() ; 
	}
	
	public String getValue(String property) {
		return preferenceStore.getString(property);
	}
	
	public void setValue(String property, String value) {
		preferenceStore.setValue(property, value);
	}
	
	public boolean getBooleanValue(String property) {
		return preferenceStore.getBoolean(property);
	}
	
	public void setBooleanValue(String property, boolean value) {
		preferenceStore.setValue(property, value);
	}
	
	public PreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(PreferenceStore store) {
		preferenceStore = store;
	}

}
