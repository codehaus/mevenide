package org.mevenide.ui.eclipse.launch.configuration;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.variables.ILaunchVariableManager;
import org.eclipse.debug.core.variables.LaunchVariableUtil;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.mevenide.ui.eclipse.Mevenide;


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
		List configurations = findExistingLaunchConfigurations(project);
		if (configurations.isEmpty()) {
			configuration = createDefaultLaunchConfiguration(project);
		} 
		else {
			if (configurations.size() == 1) {
				configuration= (ILaunchConfiguration)configurations.get(0);
			} else {
				configuration= chooseConfig(configurations);
				if (configuration == null) {
					// User cancelled selection
					return;
				}
			}
		}
		
		
		
		if (configuration != null) {
			if ( showDialog ) {
				//IStatus status = new Status(IStatus.INFO, Mevenide.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				DebugUITools.openLaunchConfigurationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), configuration, IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP, null);
			}
			DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
		}
	} 
	
	private List findExistingLaunchConfigurations(IProject project) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.mevenide.launching.MavenLaunchConfigType");
		List validConfigs= new ArrayList();
		if (type != null) {
			ILaunchConfiguration[] configs = null;
			try {
				configs = manager.getLaunchConfigurations(type);
			} 
			catch (CoreException e) {
				log.debug("Unable to retrieve previous launch configs due to : " + e);
			}
			if (configs != null && configs.length > 0) {
				IPath filePath = project.getLocation();
				if (filePath == null) {
					log.debug("Project location shouldnot be null");
				} 
				else {
					for (int i = 0; i < configs.length; i++) {
						ILaunchConfiguration configuration = configs[i];
						IPath location;
						try {
							location = ExternalToolsUtil.getWorkingDirectory(configuration);
							if (filePath.equals(location)) {
								validConfigs.add(configuration);
							}
						} catch (CoreException e) {
							// error occurred in variable expand - ignore
						}
					}
				}
			}
		}
		return validConfigs;
	}
	

	public static ILaunchConfiguration createDefaultLaunchConfiguration(IProject project) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.mevenide.launching.MavenLaunchConfigType");
		String name = project.getName();
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		try {
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);
			workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY,
				LaunchVariableUtil.newVariableExpression(ILaunchVariableManager.VAR_WORKSPACE_LOC, project.getFullPath().toString()));
			workingCopy.setAttribute(MavenOptionsTab.GOALS_TO_RUN, Mevenide.getPlugin().getDefaultGoals());
			
			// set default for common settings
			CommonTab tab = new CommonTab();
			tab.setDefaults(workingCopy);
			tab.dispose();
			
			return workingCopy.doSave();
		} 
		catch (CoreException e) {
			log.debug("Unable to createDefaultLaunchConfig due to : " + e);
			return null;
		}
	}
	
	public static ILaunchConfiguration chooseConfig(List configs) {
		if (configs.isEmpty()) {
			return null;
		}
		ILabelProvider labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(Display.getDefault().getActiveShell(), labelProvider);
		dialog.setElements((ILaunchConfiguration[]) configs.toArray(new ILaunchConfiguration[configs.size()]));
		dialog.setTitle("Configuration Choice"); //$NON-NLS-1$
		dialog.setMessage("Select the configuration to run"); //$NON-NLS-1$
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}
	
}
