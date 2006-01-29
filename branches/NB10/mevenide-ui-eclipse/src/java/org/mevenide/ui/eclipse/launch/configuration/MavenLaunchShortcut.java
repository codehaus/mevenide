/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.launch.configuration;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenLaunchShortcut implements ILaunchShortcut {
	
    private static Log log = LogFactory.getLog(MavenLaunchShortcut.class);

    private static final String MAVEN_LAUNCH_CONFIG_TYPE = "org.mevenide.ui.launching.MavenLaunchConfigType"; //$NON-NLS-1$
	
    private boolean showDialog = true;

	private String goalsToRun = null;
	private boolean offline = false;
	
	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;

	}

	public void launch(IEditorPart editor, String mode) {
		//do nothing for now
	}

	public void launch(ISelection selection, String mode) {
		if ( selection instanceof IStructuredSelection ) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			IResource resource = null;

			Object object = structuredSelection.getFirstElement();
			if (object instanceof IAdaptable) {
				resource = (IResource)((IAdaptable)object).getAdapter(IResource.class);
			}

			//IProject project = null;
			IPath basedir = null;
			if ( resource instanceof IContainer ) {
				IContainer container = (IContainer) resource;
				basedir = container.getFullPath();
			}
			if ( resource instanceof IFile ) {
				//project = resource.getProject();
				basedir = resource.getParent().getFullPath();
			}
			
			
			if ( basedir != null ) {
				log.debug("launching from basedir : " + basedir); //$NON-NLS-1$
				launch(basedir);
			}
			else {
				log.debug("Unable to get basedir"); //$NON-NLS-1$
			}
		}

	}

	public void launch(IPath basedir) {
		ILaunchConfiguration configuration= null;
		configuration = getDefaultLaunchConfiguration(basedir);

		if (configuration != null) {
			if ( showDialog ) {
				//IStatus status = new Status(IStatus.INFO, Mevenide.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				int val = DebugUITools.openLaunchConfigurationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), configuration, IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP, null);
				if ( val == Window.CANCEL ) {
					try {
						configuration.delete();
					}
					catch ( Exception e ) {
						log.debug("Exception while cancelling launch : ", e ); //$NON-NLS-1$
					}
					return;
				}
				
			}
			
			String newName= DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(configuration.getName());
			try {
			    if ( configuration.exists() ) {
			        configuration = configuration.copy(newName);
			    }
				if (showDialog) {
					configuration = configuration.getWorkingCopy().doSave();
				}
				else {
				    //caused config to be launched twice (see MavenLaunchDelegate)
				    DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
				}
			}
			catch (Exception e) {
				log.error("Unable to copy configuration due to : ", e); //$NON-NLS-1$
			}
		}
	} 
	
	
	
	private ILaunchConfiguration getDefaultLaunchConfiguration(IPath basedir) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(MAVEN_LAUNCH_CONFIG_TYPE);
		
		String name = "[" + basedir.lastSegment() + "] "; //$NON-NLS-1$ //$NON-NLS-2$
		String goals = goalsToRun == null ? Mevenide.getInstance().getCustomPreferenceStore().getString(MevenidePreferenceKeys.MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY) : goalsToRun;
		name += StringUtils.replace(goals, Goal.SEPARATOR, "_"); //$NON-NLS-1$
		
		ILaunch[] launches = manager.getLaunches();
		if ( launches != null ) {
		    //do we still want that behaviour ? is it not better to always return a new configuration ? 
			for (int i = 0; i < launches.length; i++) {
				if ( name != null && 
				        launches[i].getLaunchConfiguration() != null && 
				        (name).equals(launches[i].getLaunchConfiguration().getName()) ) {
					return launches[i].getLaunchConfiguration();
				}	
			}
		}
		
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		
		try {
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);
			workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY,
			        VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression("workspace_loc", basedir.toString()));  //$NON-NLS-1$
			workingCopy.setAttribute(MavenArgumentsTab.GOALS_TO_RUN, goals);
			
			if ( offline ) {
				Map optionsMap = new HashMap();
				optionsMap.put("o", Boolean.toString(offline)); //$NON-NLS-1$
				workingCopy.setAttribute(MavenArgumentsTab.OPTIONS_MAP, optionsMap);
			}
			
		    // set default for common settings
			CommonTab tab = new CommonTab();
			tab.setDefaults(workingCopy);
			tab.dispose();
			
			ILaunchConfiguration cfg = workingCopy.doSave();

			log.debug("returning default config : " + cfg) ; //$NON-NLS-1$

			return cfg;

		} 
		catch (CoreException e) {
			log.debug("Unable to createDefaultLaunchConfig due to : " + e); //$NON-NLS-1$
			return null;
		}
	}
	
	public void setGoalsToRun(String goalsToRun) {
		this.goalsToRun = goalsToRun;
	}
	
	public void setOffline(boolean offline) {
		this.offline = offline;
	}
}
