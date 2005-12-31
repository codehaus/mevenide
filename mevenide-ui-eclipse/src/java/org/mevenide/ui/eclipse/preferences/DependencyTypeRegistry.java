/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyTypeRegistry {

    public static final String TYPE_JAR = "jar"; //$NON-NLS-1$
    public static final String TYPE_EJB = "ejb"; //$NON-NLS-1$
    public static final String TYPE_PLUGIN = "plugin"; //$NON-NLS-1$
    public static final String TYPE_ASPECT = "aspect"; //$NON-NLS-1$
    public static final String TYPE_WAR = "war"; //$NON-NLS-1$

    public static final String[] KNOWN_TYPES = new String[] {
        TYPE_JAR,
        TYPE_EJB,
        TYPE_PLUGIN,
        TYPE_ASPECT,
        TYPE_WAR
    };

    private DependencyTypeRegistry() {
    }

    public static String[] getUserRegisteredTypes() {
        String registeredTypes = getPreferenceStore().getString(MevenidePreferenceKeys.REGISTERED_DEPENPENCY_TYPES);
        return (registeredTypes == null) ? new String[0] : registeredTypes.split(","); //$NON-NLS-1$
    }
    
    public static String[] getAllRegisteredTypes() {
        String[] userPrefTypes = getUserRegisteredTypes();
        String[] allPrefTypes = new String[userPrefTypes.length + KNOWN_TYPES.length];
        System.arraycopy(KNOWN_TYPES, 0, allPrefTypes, 0, KNOWN_TYPES.length);
        System.arraycopy(userPrefTypes, 0, allPrefTypes, KNOWN_TYPES.length, userPrefTypes.length);
        return allPrefTypes;
    }

    public static boolean storeTypes(List types) {
        final List knownTypes = Arrays.asList(KNOWN_TYPES);

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < types.size(); ++i) {
            if (!knownTypes.contains(types.get(i))) {
    			if (buffer.length() > 0) buffer.append(','); //$NON-NLS-1$
    			buffer.append((String)types.get(i));
            }
        }

        getPreferenceStore().setValue(
        	MevenidePreferenceKeys.REGISTERED_DEPENPENCY_TYPES, 
        	buffer.toString()
        );
        
        return commitChanges();
    }

    /**
     * Saves the changes made to preferences.
     * @return <tt>true</tt> if the preferences were saved
     */
    private static boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Unable to save preferences.", e);
        }

        return false;
    }

    /**
     * @return the preference store to use in this object
     */
    private static IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
