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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
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
public class DynamicPreferencePageFactory {
    
	private static final String MAIN_PREFERENCE_PAGE_PATH = "org.mevenide.ui.plugin.preferences.MavenPreferencePage";
    private static final String ROOT_PAGE_PATH = MAIN_PREFERENCE_PAGE_PATH + "/org.mevenide.ui.eclipse.preferences.PluginsRoot";
    
    private static final String PLUGIN_DESCRIPTION = "description";
	private static final String PLUGIN_PROPERTY = "property";

    private static final String PAGE_ID = "id";
	private static final String PAGE_NAME = "name";
	
	private static final String PROPERTY_DEFAULT = "default";
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_LABEL = "label";
	private static final String PROPERTY_TYPE = "type";
	private static final String PROPERTY_REQUIRED = "required";
	private static final String PROPERTY_DESCRIPTION = "description";
	
	private static DynamicPreferencePageFactory factory = new DynamicPreferencePageFactory();	
	
	public static DynamicPreferencePageFactory getFactory() {
        return factory;
    }
    
    public void createPages() {
        
        IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint("org.mevenide.ui.preference");
        if ( extension != null ) {
	        IConfigurationElement[] configurationElements = extension.getConfigurationElements();
	        for (int i = 0; i < configurationElements.length; i++) {
	            IConfigurationElement configurationElement = configurationElements[i]; 
	            IPreferenceNode node = createPreferenceNode(configurationElement);
	            Mevenide.getInstance().getWorkbench().getPreferenceManager().addTo(ROOT_PAGE_PATH, node);
	        }
        }
    }

    private IPreferenceNode createPreferenceNode(IConfigurationElement configurationElement) {
        //plugin-provider name and id
        String pageName = configurationElement.getAttribute(PAGE_NAME);
        String pageId = configurationElement.getAttribute(PAGE_ID);
        
        //plugin-provider description
        IConfigurationElement[] descriptionElements = configurationElement.getChildren(PLUGIN_DESCRIPTION);
        
        
        //plugin-provider properties
        IConfigurationElement[] propertyElements = configurationElement.getChildren(PLUGIN_PROPERTY);
        List properties = new ArrayList(propertyElements.length);
        for (int i = 0; i < propertyElements.length; i++) {
            IConfigurationElement childElement = propertyElements[i];
            String propertyName = childElement.getAttribute(PROPERTY_NAME);
            String propertyDefault = childElement.getAttribute(PROPERTY_DEFAULT);
            String propertyType = childElement.getAttribute(PROPERTY_TYPE);
            String propertyLabel = childElement.getAttribute(PROPERTY_LABEL);
            String propertyRequired = childElement.getAttribute(PROPERTY_REQUIRED);
            String propertyDescription = childElement.getAttribute(PROPERTY_DESCRIPTION);
            properties.add(new PluginProperty(pageId, 
                    						  propertyName, 
                    						  propertyLabel, 
                    						  propertyDefault, 
                    						  propertyType, 
                    						  "true".equals(propertyRequired),
                    						  propertyDescription));
        }
        DynamicPreferencePage page = new DynamicPreferencePage();
        page.setTitle(pageName);
        page.setProperties(properties);
        IPreferenceNode node = new PreferenceNode(pageId, page);
        return node;
    }
    
}
