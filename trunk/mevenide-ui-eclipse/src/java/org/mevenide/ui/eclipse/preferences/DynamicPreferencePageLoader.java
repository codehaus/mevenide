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

import java.util.Hashtable;
import java.util.Map;
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

    private static final String PAGE_ID = "id";
	private static final String PAGE_NAME = "name";
	
	private static final String PROPERTY_DEFAULT = "default";
	private static final String PROPERTY_NAME = "name";
	
	
    public void registryChanged(IRegistryChangeEvent event) {
        IExtension extension = Platform.getExtensionRegistry().getExtension("org.mevenide.ui.preference");
        if ( extension != null ) {
	        IConfigurationElement[] configurationElements = extension.getConfigurationElements();
	        for (int i = 0; i < configurationElements.length; i++) {
	            IConfigurationElement configurationElement = configurationElements[i]; 
	            IPreferenceNode node = createPreferenceNode(configurationElement);
	            Mevenide.getInstance().getWorkbench().getPreferenceManager().addTo(MAIN_PREFERENCE_PAGE_PATH, node);
	        }
        }
    }

    private IPreferenceNode createPreferenceNode(IConfigurationElement configurationElement) {
        //plugin-provider name and id
        String pageName = configurationElement.getAttribute(PAGE_NAME);
        String pageId = configurationElement.getAttribute(PAGE_ID);
        
        //plugin-provider properties
        IConfigurationElement[] childrenElements = configurationElement.getChildren("property");
        Map properties = new Hashtable(childrenElements.length);
        for (int i = 0; i < childrenElements.length; i++) {
            IConfigurationElement childElement = childrenElements[i];
            String propertyName = childElement.getAttribute(PROPERTY_NAME);
            String propertyDefault = childElement.getAttribute(PROPERTY_DEFAULT);
            properties.put(propertyName, propertyDefault);
        }
        DynamicPreferencePage page = new DynamicPreferencePage();
        page.setTitle(pageName);
        page.setProperties(properties);
        IPreferenceNode node = new PreferenceNode(pageId, page);
        return node;
    }
    
}
