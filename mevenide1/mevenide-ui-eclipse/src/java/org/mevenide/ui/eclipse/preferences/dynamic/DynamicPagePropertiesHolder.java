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

import java.util.List;


/**  
 * 
 * needed to obtain a consistent behaviour (f.i. disposed page cause troubles if not correctly handled) 
 * much has been taken from superclass 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPagePropertiesHolder {

    private String pageId;
    private String pageTitle;
    private String pluginDescription;
    
    private List pluginCategories;
    
    public DynamicPagePropertiesHolder(String pageId, String pageTitle, String pluginDescription, List categories) {
        this.pageId = pageId;
        this.pageTitle = pageTitle; 
        this.pluginCategories = categories;
        this.pluginDescription = pluginDescription;
    }
    
    void initialize(DynamicPreferencePage page) {
        //page.setTitle(pageTitle);
        page.setCategories(pluginCategories);
        page.setPluginDescription(pluginDescription);
        page.setPluginName(pageTitle);    
    }
    	
    String getId() {
        return pageId;
    }
}
