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

package org.mevenide.context;

import java.io.File;

/**
 * interface to externalize the project files content/models to one place, and reuse
 * in all the queries (ILocationFinder, IPropertyResolver, etc)
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public interface IQueryContext
{
    String getPropertyValue(String key);
    
    String getUserPropertyValue(String key);
    
    String getBuildPropertyValue(String key);
    
    String getProjectPropertyValue(String key);
    
    File getProjectDirectory();
    
    File getUserDirectory();
    
    IProjectContext getPOMContext();
}
