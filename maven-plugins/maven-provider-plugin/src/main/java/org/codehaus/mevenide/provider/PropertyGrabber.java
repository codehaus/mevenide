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
package org.codehaus.mevenide.provider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.Element;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyGrabber {
    
    private static final Log log = LogFactory.getLog(PropertyGrabber.class);
    
    private File propertyFile;
    
    private String elementHandlerClassName;

    private IElementHandler elementHandler;
    
    private String propertyDescription;
    
    private String pluginName;
    
    private String pluginVersion;
    
    public void grab() throws GrabberException {
        createElementHandler();
        
        try {
            elementHandler.setPluginName(pluginName);
            elementHandler.setPluginVersion(pluginVersion);
            PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(propertyFile);
            List modelElements = model.getList();
            for (int i = 0; i < modelElements.size(); i++) {
                Element element = (Element) modelElements.get(i);
                elementHandler.handle(element);
            }
            
            propertyDescription = elementHandler.getXmlDescription();
            
        }
        catch (IOException e) {
            String message = "unable to create grab properties"; //$NON-NLS-1$ 
            log.error(message, e);
        }
    }
    
    private void createElementHandler() {
        try {
            elementHandler = (IElementHandler) Class.forName(elementHandlerClassName).newInstance();
        }
        catch (Exception e) {
            elementHandler = new DefaultElementHandler();
        }
    }

    public String getElementHandlerClassName() {
        return elementHandlerClassName;
    }
    
    public void setElementHandlerClassName(String elementHandlerClassName) {
        this.elementHandlerClassName = elementHandlerClassName;
    }
    
    public String getPropertyDescription() {
        return propertyDescription;
    }

    public File getPropertyFile() {
        return propertyFile;
    }
    
    public void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }
    
    public String getPluginName() {
        return pluginName;
    }
    
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }
    
    public String getPluginVersion() {
        return pluginVersion;
    }
    
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
}
