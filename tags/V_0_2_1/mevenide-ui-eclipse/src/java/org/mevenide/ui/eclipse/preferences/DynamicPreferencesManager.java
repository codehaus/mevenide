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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencesManager extends PreferencesManager {
    private static final DynamicPreferencesManager manager = new DynamicPreferencesManager();

    public static final String SEPARATOR = "|"; //$NON-NLS-1$
    
    protected DynamicPreferencesManager() {
        super();
    }
    
    protected String getPreferenceStoreFilename() {
	    return Mevenide.getInstance().getDynamicPreferencesFilename();
	}
    
	public static DynamicPreferencesManager getDynamicManager()  {
		return manager;
	}
	
	public Map getPreferences() {
	    Map preferences = super.getPreferences();
	    Map dynamicPreferences = new HashMap();
	    for (Iterator it = preferences.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String value = (String) preferences.get(key);
            dynamicPreferences.put(key.indexOf(SEPARATOR) != -1 ? key.substring(key.indexOf(SEPARATOR) + 1, key.length()) : key, value);
        }
	    return dynamicPreferences;
	}
}