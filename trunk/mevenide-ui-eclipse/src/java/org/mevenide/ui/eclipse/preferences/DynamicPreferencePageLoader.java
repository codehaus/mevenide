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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceNode;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencePageLoader implements IRegistryChangeListener  {
    private static final String MAIN_PREFERENCE_PAGE_PATH = "org.mevenide.ui.plugin.preferences.MavenPreferencePage";
    
    private void initDynamicPreferencePages() {
        IExtension extension = Platform.getExtensionRegistry().getExtension("org.mevenide.ui.preference");
        System.err.println(extension == null);
        if ( extension != null ) {
	        IConfigurationElement[] configurationElements = extension.getConfigurationElements();
	        System.err.println(configurationElements.length);
	        for (int i = 0; i < configurationElements.length; i++) {
	            IPreferenceNode node = new PreferenceNode("org.toto", "Toto", null, DynamicPreferencePage.class.getName());
	            Mevenide.getInstance().getWorkbench().getPreferenceManager().addTo(MAIN_PREFERENCE_PAGE_PATH, node);
	            System.err.println(configurationElements[i].getAttribute("name"));
	            System.err.println(configurationElements[i].getAttribute("default"));
	        }
        }
    }

    public void registryChanged(IRegistryChangeEvent event) {
        System.err.println("reg changed : ");
        for (int i = 0; i < event.getExtensionDeltas().length; i++) {
            System.err.println("\t" + event.getExtensionDeltas()[i].getExtension() + "\t" + event.getExtensionDeltas()[i].getExtensionPoint());
        }
	    //IPreferenceNode node = new PreferenceNode("org.toto", "Toto", null, DynamicPreferencePage.class.getName());
        //getWorkbench().getPreferenceManager().addTo(MAIN_PREFERENCE_PAGE_PATH, node);
        initDynamicPreferencePages();
    }
    
}
