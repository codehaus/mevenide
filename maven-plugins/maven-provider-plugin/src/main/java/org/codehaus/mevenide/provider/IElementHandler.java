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

import org.mevenide.properties.Element;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public interface IElementHandler {

    static final String DOCTYPE = "\t<!ELEMENT plugin (property*)>\n" +  //$NON-NLS-1$
                                  "\t<!ATTLIST plugin name CDATA #IMPLIED> \n" +  //$NON-NLS-1$
                                  "\t<!ATTLIST property \n" +  //$NON-NLS-1$
                                  "\t          name CDATA #REQUIRED \n" +  //$NON-NLS-1$
                                  "\t          label CDATA #IMPLIED \n" +  //$NON-NLS-1$
                                  "\t          default CDATA #IMPLIED \n" +  //$NON-NLS-1$
                                  "\t          required (true|false) \"false\" \n" +  //$NON-NLS-1$
                                  "\t          description CDATA #IMPLIED \n" +  //$NON-NLS-1$
                                  "\t          validator CDATA #IMPLIED \n" +  //$NON-NLS-1$
                                  "\t          validate (true|false) \"true\" \n" +  //$NON-NLS-1$
                                  "\t          scope (project|global) \"project\" \n" +  //$NON-NLS-1$
                                  "\t          category CDATA #IMPLIED>\n"; //$NON-NLS-1$

    static final String DEFAULT_ATTR = "default"; //$NON-NLS-1$
    static final String DESCRIPTION_ATTR = "description"; //$NON-NLS-1$
    static final String INDENT = "    "; //$NON-NLS-1$
    static final String NAME_ATTR = "name"; //$NON-NLS-1$
    static final String PLUGIN_ELEMENT = "plugin"; //$NON-NLS-1$
    static final String PLUGIN_NAME = "name"; //$NON-NLS-1$
    static final String PLUGIN_VERSION = "version"; //$NON-NLS-1$
    static final String PROPERTY_ELEMENT = "property"; //$NON-NLS-1$
    
    void handle(Element element);

    String getXmlDescription();
    
    void setPluginName(String pluginName);
    
    void setPluginVersion(String pluginVersion);
}
