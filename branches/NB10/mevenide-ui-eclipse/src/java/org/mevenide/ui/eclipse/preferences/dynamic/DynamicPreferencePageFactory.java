/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.preferences.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencePageFactory {
    
	private boolean isInitialized;
    
	private Map propertiesHolders = new HashMap();

    private static final String EXTENSION_ID = "org.mevenide.ui.preference"; //$NON-NLS-1$
    
    private static final String PLUGIN_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String PLUGIN_PROPERTY = "property"; //$NON-NLS-1$
	private static final String PLUGIN_CATEGORY = "category"; //$NON-NLS-1$

    private static final String PAGE_ID = "id"; //$NON-NLS-1$
	private static final String PAGE_NAME = "name"; //$NON-NLS-1$

	private static final String CATEGORY_NAME = "name"; //$NON-NLS-1$
	
	private static final String PROPERTY_DEFAULT = "default"; //$NON-NLS-1$
	private static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	private static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$
	private static final String PROPERTY_TYPE = "type"; //$NON-NLS-1$
	private static final String PROPERTY_REQUIRED = "required"; //$NON-NLS-1$
	private static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	
	private static DynamicPreferencePageFactory factory = new DynamicPreferencePageFactory();	
	
	public static DynamicPreferencePageFactory getFactory() {
	    if ( !factory.isInitialized ) {
	        factory.createDynamicPagePropertiesHolder();
	        factory.isInitialized = true;
	    }
        return factory;
    }
    
	
	
    public void createDynamicPagePropertiesHolder() {
        IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_ID); 
        if ( extension != null ) {
	        IConfigurationElement[] configurationElements = extension.getConfigurationElements();
	        for (int i = 0; i < configurationElements.length; i++) {
	            IConfigurationElement configurationElement = configurationElements[i]; 
	            DynamicPagePropertiesHolder holder = createDynamicPagePropertiesHolder(configurationElement);
	            propertiesHolders.put(holder.getId(), holder);
	        }
        }
    }

    private DynamicPagePropertiesHolder createDynamicPagePropertiesHolder(IConfigurationElement configurationElement) {
        //plugin-provider name and id
        String pageName = configurationElement.getAttribute(PAGE_NAME);
        String pageId = configurationElement.getAttribute(PAGE_ID);
        
        //plugin-provider description
        IConfigurationElement[] descriptionElements = configurationElement.getChildren(PLUGIN_DESCRIPTION);
        String pluginDescription = null;
        if ( descriptionElements.length > 0 ) {
            pluginDescription = descriptionElements[0].getValue();
        }
        
        //plugin-provider categories
        IConfigurationElement[] categoryElements = configurationElement.getChildren(PLUGIN_CATEGORY);
        List categories = new ArrayList(categoryElements.length);
        for (int i = 0; i < categoryElements.length; i++) {
            IConfigurationElement categoryElement = categoryElements[i];
            String categoryName = categoryElement.getAttribute(CATEGORY_NAME);
	        //plugin-provider properties
            List properties = getCategoryProperties(pageId, categoryElement);
            categories.add(new PluginCategory(categoryName, properties));
        }
        
        DynamicPagePropertiesHolder holder = new DynamicPagePropertiesHolder(pageId, pageName, pluginDescription, categories);
        return holder;
    }

    private List getCategoryProperties(String pageId, IConfigurationElement categoryElement) {
        IConfigurationElement[] propertyElements = categoryElement.getChildren(PLUGIN_PROPERTY);
        List properties = new ArrayList(propertyElements.length);
        for (int j = 0; j < propertyElements.length; j++) {
            IConfigurationElement childElement = propertyElements[j];
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
                    						  "true".equals(propertyRequired), //$NON-NLS-1$
                    						  propertyDescription));
        }
        return properties;
    }



    public void initialize(DynamicPreferencePage page) {
        DynamicPagePropertiesHolder holder = (DynamicPagePropertiesHolder) propertiesHolders.get(page.getTitle());
        if ( holder != null ) {
            holder.initialize(page);
        }
    }
    
}
