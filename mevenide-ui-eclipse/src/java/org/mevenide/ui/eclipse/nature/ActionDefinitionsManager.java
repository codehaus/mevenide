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
import java.util.List;
import org.eclipse.core.resources.IProject;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionDefinitionsManager implements IActionDefinitionManager {
    private List definitions = new ArrayList();

    
    public ActionDefinitionsManager() {
//	      Just to stub.. even if the interface was introduced for this purpose.. 
//		  need to create a proper stub implementation        
        ActionDefinitions def = new ActionDefinitions();
        List patterns = new ArrayList();
        patterns.add("**/*.java");
        def.setPatterns(patterns);
        List goals = new ArrayList();
        goals.add("jar:install");
        def.setGoals(goals);
        definitions.add(def);
    }
    
    public List getDefinitions(IProject project) {
        return definitions;
    }
    
}
