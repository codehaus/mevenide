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

package org.mevenide.context;

import java.io.File;
import org.apache.maven.project.Project;
import org.jdom.Element;

/**
 * extending the IQueryContext with the POM.
 * All the returned objects are intented for short-term use and shall not be 
 * modified from outside.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */


public interface IProjectContext
{
    /**
     * the merged result of the project.xml POM and all it's precedessors. ("extends" tag)
     */
    Project getFinalProject();
    /**
     * Separated project instances for each of files in the row. The current POM file is first,
     *it's parent is next and the parent of all is last.
     */
    Project[] getProjectLayers();
    
    /**
     * Separated File locations of all the POM files in the succession row. The current POM file is first,
     *it's parent is next and the parent of all is last.
     */
    File[] getProjectFiles();
    
    /**
     * merged content of the JDOM tree describing the POM.
     */
    Element getRootProjectElement();
    
    /**
     * content of the JDOM trees describing the POM, split into the layers (extends)
     *
     */
    Element[] getRootElementLayers();
}
