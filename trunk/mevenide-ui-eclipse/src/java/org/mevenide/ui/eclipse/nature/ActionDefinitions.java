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
package org.mevenide.ui.eclipse.nature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionDefinitions {

    private Map perProjectEnablementMap;
    
    private ILaunchConfiguration configuration;
    
    
    public ActionDefinitions() {
        perProjectEnablementMap = new HashMap();
    }
    
    public boolean isEnabled(IProject project) {
        if ( project == null ) {
            return false;
        }
        if ( project != null && !project.exists() ) {
            perProjectEnablementMap.remove(project.getLocation().toString());
            return false;
        }
        return project != null && 
               perProjectEnablementMap.containsKey(project.getLocation().toString()) && 
               ((Boolean) perProjectEnablementMap.get(project.getLocation().toString())).booleanValue(); 
         
    }
    
    public void setEnabled(IProject project, boolean enabled) {
        perProjectEnablementMap.put(project.getLocation().toString(), Boolean.valueOf(enabled));
    }
    
    
    
    public ILaunchConfiguration getConfiguration() {
        return configuration;
    }
    public void setConfiguration(ILaunchConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public String getGoalList() {
        String list = "";
        try {
            String goals = configuration.getAttribute("GOALS_TO_RUN", "");
            return goals;
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List getPatterns() {
        List list = new ArrayList();
        try {
            list = configuration.getAttribute("PATTERNS", new ArrayList());
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean equals(Object obj) {
        return obj instanceof ActionDefinitions && 
               this.configuration.equals(((ActionDefinitions) obj).configuration);  
    }
}
