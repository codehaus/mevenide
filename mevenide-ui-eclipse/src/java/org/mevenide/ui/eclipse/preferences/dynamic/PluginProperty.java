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

import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PluginProperty {
    
    private String pageId;

    private String name;
    private String label;
    private String defaultValue;
    private String type;
    private boolean required;
    private String description;
    
    public PluginProperty(String pageId, 
            				String name, 
            				String propertyLabel, 
            				String defaultValue, 
            				String type,
            				boolean required,
            				String description) {
        this.pageId = pageId;
        this.name = name;
        this.label = propertyLabel;
        this.defaultValue = defaultValue;
        this.type = type;
        this.required = required;
        this.description = description;
    }
    
    public String getDefault() {
        return defaultValue;
    }
    
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getPageId() {
        return pageId;
    }
    
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public boolean equals(Object obj) {
        if ( obj == null || !(obj instanceof PluginProperty) ) {
            return false;
        }
        PluginProperty property = (PluginProperty) obj;
        return StringUtils.relaxEqual(this.defaultValue, property.defaultValue) &&
        	   StringUtils.relaxEqual(this.description, property.description) &&
        	   StringUtils.relaxEqual(this.label, property.label) &&
        	   StringUtils.relaxEqual(this.name, property.name) &&
        	   StringUtils.relaxEqual(this.pageId, property.pageId) &&
        	   StringUtils.relaxEqual(this.type, property.type) ;
    }
}
