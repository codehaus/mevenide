/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	
	private static PreferencesManager manager = new PreferencesManager();
	
	private PreferenceStore preferenceStore;
	
	protected PreferencesManager() {
	    loadPreferences();
	}
	
	public static PreferencesManager getManager()  {
		return manager;
	}
	
	public void loadPreferences() {
		preferenceStore = new PreferenceStore(getPreferenceStoreFilename());
		try {
			preferenceStore.load();
		}
		catch ( Exception ex ) {
			log.debug("Unable to load preferences from file '" + getPreferenceStoreFilename(), ex); //$NON-NLS-1$
		}
	}
	
	public boolean store() {
		try {
			preferenceStore.save();		
			return true;
		}
		catch (IOException e) {
			log.debug("Unable to save preferences to file '" + getPreferenceStoreFilename(), e); //$NON-NLS-1$
			return false;
		}
	}
	
	protected String getPreferenceStoreFilename() {
	    return Mevenide.getInstance().getPreferencesFilename();
	}
	
	public String getValue(String property) {
		return preferenceStore.getString(property);
	}
	
	public void setValue(String property, String value) {
	    if ( preferenceStore == null ) {
	        loadPreferences();
	    }
		preferenceStore.setValue(property, value);
	}
	
	public boolean getBooleanValue(String property) {
		return preferenceStore.getBoolean(property);
	}
	
	public int getIntValue(String property) {
		return preferenceStore.getInt(property);
	}
	
	public void setBooleanValue(String property, boolean value) {
		preferenceStore.setValue(property, value);
	}
	
	public void setIntValue(String property, int value) {
		preferenceStore.setValue(property, value);
	}
		
	public PreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(PreferenceStore store) {
		preferenceStore = store;
	}

	public Map getPreferences() {
	    Map preferences = new HashMap();
	    //initialize preferenceStore if not already done
	    if ( getPreferenceStore() == null ) {
	        loadPreferences();
	    }
	    try {
            String[] names = preferenceStore.preferenceNames();
            if ( names != null ) {
                for (int i = 0; i < names.length; i++) {
                    preferences.put(names[i], getValue(names[i]));
                }
            }
        }
        catch (NullPointerException e) {
            //catch NPE thrown if preferenceStore cannot be properly initialized
            String message = "No preferences found";  //$NON-NLS-1$
            log.info(message, e);
        }
	    
	    return preferences;
	}
	
	public void remove(String key) {
	    preferenceStore.setToDefault(key);
	}
}
