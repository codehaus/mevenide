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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyTypeRegistry {

    private DependencyTypeRegistry() {
    }

    public static String[] getUserRegisteredTypes() {
        List prefTypes = new ArrayList();
        String registeredTypes = getPreferenceStore().getString(MevenidePreferenceKeys.REGISTERED_DEPENPENCY_TYPES);
        if ( !StringUtils.isNull(registeredTypes) ) {
            StringTokenizer tokenizer = new StringTokenizer(registeredTypes, ","); //$NON-NLS-1$
            while ( tokenizer.hasMoreTokens() ) {
                String type = tokenizer.nextToken();
                if ( !StringUtils.isNull(type) ) {
                    prefTypes.add(type);
                }
            }
        }
        return (String[]) prefTypes.toArray(new String[prefTypes.size()]);
    }

    public static boolean storeTypes(List types) {
        String registeredTypes = ""; //$NON-NLS-1$
        for (int i = 0; i < types.size(); i++) {
            if ( !Arrays.asList(Mevenide.KNOWN_DEPENDENCY_TYPES).contains(types.get(i)) ) {
                registeredTypes += (String) types.get(i) + ","; //$NON-NLS-1$
            }
        }
        getPreferenceStore().setValue(
        	MevenidePreferenceKeys.REGISTERED_DEPENPENCY_TYPES, 
        	registeredTypes
        );
        
        return commitChanges();
    }

    /**
     * TODO: Describe what commitChanges does.
     * @return
     */
    private static boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Internal MevenIDE Error", "Unable to save preferences.", e);
        }

        return false;
    }

    /**
     * TODO: Describe what getPreferenceStore does.
     * @return
     */
    private static IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
