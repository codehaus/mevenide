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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    
    public static final String BUILD_CONFIG_TYPE = "org.mevenide.ui.launching.ActionDefinitionConfigType"; //$NON-NLS-1$
    public static final String AUTO_BUILD = "AUTO_BUILD"; //$NON-NLS-1$
    public static final String PATTERNS_LIST = "PATTERNS_LIST"; //$NON-NLS-1$
    static final String CUSTOM_CONFIG_LAUNCHGROUP_ID = "org.mevenide.ui.launching.custom.LaunchGroup"; //$NON-NLS-1$ 
    static final String CUSTOM_CONFIG_TABGROUP_ID = "org.mevenide.ui.launching.ActionDefinitionTabGroup"; //$NON-NLS-1$
    
    private List definitions = new ArrayList();
    
    private static final Log log = LogFactory.getLog(ActionDefinitions.class);

    
    public ActionDefinitionsManager() {
        DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(this);
        try {
            ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(BUILD_CONFIG_TYPE);
            ILaunchConfiguration[] configurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type);
            for (int i = 0; i < configurations.length; i++) {
                ILaunchConfiguration configuration = configurations[i];
                if ( !isSharedInfoConfiguration(configuration) ) { //$NON-NLS-1$
                    ActionDefinitions definition = new ActionDefinitions();
	                definition.setConfiguration(configuration);
	                definitions.add(definition);
                }
            }
        }
        catch (CoreException e) {
            log.error("Unable to retrieve launch configurations", e); //$NON-NLS-1$
        }
    }
    
    private boolean isSharedInfoConfiguration(ILaunchConfiguration configuration) {
        return configuration.getName() != null && configuration.getName().indexOf(BUILD_CONFIG_TYPE + ".SHARED_INFO") != -1;
    }

    public List getDefinitions() {
        return definitions;
    }
    
    public void launchConfigurationAdded(ILaunchConfiguration configuration) {
        try {
            
            if ( isMevenideCustomConfiguration(configuration) && !isSharedInfoConfiguration(configuration) ) {
		        ActionDefinitions definition = new ActionDefinitions();
		        definition.setConfiguration(configuration);
		        definitions.add(definition);
	        }
        }
        catch (CoreException e) {
            log.error("Unable to retrieve launch configurations", e); //$NON-NLS-1$
        }   
    }
    
    private boolean isMevenideCustomConfiguration(ILaunchConfiguration configuration) throws CoreException {
        return configuration.getType() != null && configuration.getType().getIdentifier().indexOf(BUILD_CONFIG_TYPE) != -1;
    }

    public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
        ActionDefinitions definition = new ActionDefinitions();
        definition.setConfiguration(configuration);
        //inconditionnally remove config definition. 
        definitions.remove(definition);
    }
    
    public void launchConfigurationChanged(ILaunchConfiguration configuration) {
    }
}
