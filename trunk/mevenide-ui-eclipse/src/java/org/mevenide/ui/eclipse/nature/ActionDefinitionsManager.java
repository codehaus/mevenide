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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionDefinitionsManager implements IActionDefinitionManager, ILaunchConfigurationListener {
    List definitions = new ArrayList();

    private static final Log log = LogFactory.getLog(ActionDefinitions.class);
    
    public ActionDefinitionsManager() {
        try {
            ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.mevenide.ui.launching.ActionDefinitionConfigType");
            ILaunchConfiguration[] configurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type);
            for (int i = 0; i < configurations.length; i++) {
                if ( configurations[i].getName() != null && configurations[i].getName().indexOf("org.mevenide.ui.launching.ActionDefinitionConfigType.SHARED_INFO") == -1 ) {
	                ActionDefinitions definition = new ActionDefinitions();
	                definition.setConfiguration(configurations[i]);
	                definitions.add(definition);
                }
            }
        }
        catch (CoreException e) {
            log.error("Unable to retrieve launch configurations", e);
        }
    }
    
    public List getDefinitions(IProject project) {
        for (Iterator it = definitions.iterator(); it.hasNext(); ) {
            ActionDefinitions definition = (ActionDefinitions) it.next();
            if ( !definition.getConfiguration().exists() ) {
                it.remove();
            }
        }
        return definitions;
    }

    
    
    public void launchConfigurationAdded(ILaunchConfiguration configuration) {
        ActionDefinitions definition = new ActionDefinitions();
        definition.setConfiguration(configuration);
        definitions.add(definition);
    }
    
    public void launchConfigurationChanged(ILaunchConfiguration configuration) {
    }
    
    public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
    }
}
