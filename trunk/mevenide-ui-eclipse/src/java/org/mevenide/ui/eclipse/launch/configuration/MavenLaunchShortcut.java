/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.launch.configuration;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.variables.ILaunchVariableManager;
import org.eclipse.debug.core.variables.LaunchVariableUtil;
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

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenLaunchShortcut implements ILaunchShortcut {
	private static Log log = LogFactory.getLog(MavenLaunchShortcut.class);

	private boolean showDialog = false;


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

			IProject project = null;
			if ( resource instanceof IProject ) {
				project = (IProject) resource;
			}
			else {
				project = resource.getProject();
			}
			if ( project != null ) {
				launch(project);
			}
			else {
				log.debug("Unable to get project..");
			}
		}

	}

	public void launch(IProject project) {
		ILaunchConfiguration configuration= null;
		configuration = getDefaultLaunchConfiguration(project);

		if (configuration != null) {
			if ( showDialog ) {
				//IStatus status = new Status(IStatus.INFO, Mevenide.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				int val = DebugUITools.openLaunchConfigurationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), configuration, IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP, null);
				if ( val == Window.CANCEL ) {
					try {
						configuration.delete();
					}
					catch ( Exception e ) {
						log.debug("Exception while cancelling launch : " + e );
					}
					return;
				}
				
			}
			
			try {
				String newName= DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(configuration.getName());
				configuration = configuration.copy(newName);
				DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
			}
			catch (Exception e) {
				// @todo Auto-generated catch block
				//e.printStackTrace();
			}
			
			
		}
	} 
	
	
	
	private ILaunchConfiguration getDefaultLaunchConfiguration(IProject project) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.mevenide.ui.launching.MavenLaunchConfigType");
		
		String name = "[" + project.getName() + "] ";
		String goals = StringUtils.replace(Mevenide.getPlugin().getDefaultGoals(), ":", "_");
		name += goals;
		
		ILaunch[] launches = manager.getLaunches();
		for (int i = 0; i < launches.length; i++) {
			if ( (name).equals(launches[i].getLaunchConfiguration().getName()) ) {
				return launches[i].getLaunchConfiguration();
			}	
		}
		
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		
		try {
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);
			workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY,
				LaunchVariableUtil.newVariableExpression(ILaunchVariableManager.VAR_WORKSPACE_LOC, project.getFullPath().toString()));
			workingCopy.setAttribute(MavenArgumentsTab.GOALS_TO_RUN, Mevenide.getPlugin().getDefaultGoals());
			
			// set default for common settings
			CommonTab tab = new CommonTab();
			tab.setDefaults(workingCopy);
			tab.dispose();
			
			ILaunchConfiguration cfg = workingCopy.doSave();

			log.debug("returning default config : " + cfg) ;

			return cfg;

		} 
		catch (CoreException e) {
			log.debug("Unable to createDefaultLaunchConfig due to : " + e);
			return null;
		}
	}

	
}
