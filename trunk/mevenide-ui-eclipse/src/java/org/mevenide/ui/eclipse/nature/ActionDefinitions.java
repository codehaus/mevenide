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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionDefinitions {

    private List goals;
    
    private List patterns;
    
    private Map perProjectEnablementMap;
    
    private String name;
    
    
    public ActionDefinitions() {
        perProjectEnablementMap = new HashMap();
    }
    
    public List getGoals() {
        return goals;
    }
    
    public void setGoals(List goals) {
        this.goals = goals;
    }
    
    public List getPatterns() {
        return patterns;
    }
    
    public void setPatterns(List patterns) {
        this.patterns = patterns;
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
    
    public String toString() {
        String displayValue = name;
        if ( StringUtils.isNull(displayValue) ) {
            displayValue = getGoalList();
        }
        return displayValue;
    }
    
    public String getGoalList() {
        String goalList = ""; //$NON-NLS-1$
        for (int i = 0; i < goals.size() - 1; i++) {
            goalList += goals.get(i) + " "; //$NON-NLS-1$
        }
        goalList += goals.get(goals.size() - 1);
        return goalList;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
