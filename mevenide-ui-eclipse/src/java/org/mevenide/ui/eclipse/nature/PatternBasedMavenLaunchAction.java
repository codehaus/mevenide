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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PatternBasedMavenLaunchAction extends Action {
    
    private static final Log log = LogFactory.getLog(PatternBasedMavenLaunchAction.class);
    
    private ActionDefinitions definition;
    
    private IProject project;
    
    public PatternBasedMavenLaunchAction(IProject project, ActionDefinitions definition) {
        this.definition = definition;
        this.project = project;
        setText(definition.getGoalList());
        setToolTipText(definition.getGoalList());
        setEnabled(definition.isEnabled(project));
    }
    
	public void run() {
	    try  {
	        ILaunchConfigurationWorkingCopy copy = definition.getConfiguration().getWorkingCopy();
            copy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, project.getLocation().toOSString());
            copy.doSave(); 
            definition.getConfiguration().launch(ILaunchManager.RUN_MODE, null);
            definition.projectLaunched(project);
	    }
        catch (CoreException e) {
            String message = "Unable to obtain LaunchConfigurationWorkingCopy"; //$NON-NLS-1$
            log.error(message, e);
        }
	    finally {
            try {
                ILaunchConfigurationWorkingCopy copy = definition.getConfiguration().getWorkingCopy();
                copy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, (String) null);
                copy.doSave();
            }
            catch (CoreException e1) {
                String message = "Unable to restore LaunchConfiguration state"; //$NON-NLS-1$
                log.error(message, e1);
            }
	        
	    }
	}
	
	
}
