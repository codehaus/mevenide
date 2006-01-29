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
package org.mevenide.ui.netbeans.creator;

import org.apache.maven.project.Project;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface ProjectPanel
{
    /**
     * will assign the properties of the project that it handles.
     */
    Project copyProject(Project project);
    
    /**
     * the panel will update according to the values of project parameter.
     */
    void setProject(Project project);
    
    /**
     * sets the observer interested in the validity changes of the panel.
     */
    void setValidateObserver(ProjectValidateObserver observer);
    
    /**
     * returns if the panel is in valid state.
     */
    boolean isInValidState();
    
    /**
     * returns a UI message describing the validity state.
     */
    String getValidityMessage();
}
