/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.project.io;

import java.util.List;

/**
 * provider of values for the CarefulProjectMarshaller.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface IContentProvider {
    
    /**
     * returns the value for given key, where key is eg. "artifactId", "logo" etc.
     */
    String getValue(String key);
    
    /**
     * returns a list of Strings,
     * @param parentKey, eg "includes" or "reports"
     * @param childKey, eg. "include" or "report"
     */
    List getValueList(String parentKey, String childKey);
    
    /**
     * returns a child content provider or null the child is null..
     */
    IContentProvider getSubContentProvider(String key);
    
    /**
     * returns a list of child content providers or null.
     * @param parentKey, eg "versions" or "dependencies"
     * @param childKey, eg. "version" or "dependency"
     */
    
    List getSubContentProviderList(String parentKey, String childKey);
    
    /**
     * special case handling for getProperties() call, expects to get a list of
     * <key>:<value> formatted string entries.
     */
    List getProperties();
    
    /**
     * returns ths underlying bean
     */
    Object getBean();
}
