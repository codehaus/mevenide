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
package org.mevenide.netbeans.project.customizer;

import java.util.List;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface ProjectPanel
{
    /**
     * 
     */
    List getChanges();
    
    /**
     * the panel will update according to the values of project parameter.
     */
    void setResolveValues(boolean resolveValues);
    
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
