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
package org.mevenide.ui.eclipse.launch.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenJob extends Job {
    
    private static final Log log = LogFactory.getLog(MavenJob.class);
    
    private ILaunchConfiguration configuration;
    private IPath basedir;
    private boolean showDialog;
    private Shell shell;
    
    public MavenJob(Shell shell, ILaunchConfiguration configuration, IPath basedir, boolean showDialog) {
        super("Maven Runner");
        this.configuration = configuration;
        this.basedir = basedir;
        this.showDialog = showDialog;
        this.shell = shell;
        setPriority(Job.BUILD);
    }
    
    transient IStatus status = null;
    
    protected IStatus run(IProgressMonitor monitor) {
        Runnable mavenRunner = new Runnable() {
            public void run() {
                runMaven();
            }
        }; 
        PlatformUI.getWorkbench().getDisplay().asyncExec(mavenRunner);
        return new Status(Status.OK, "org.mevenide.ui", 0, Mevenide.getResourceString("MavenRunner.Done"), null); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void runMaven() {
        if (configuration != null) {
			if ( showDialog ) {
				//IStatus status = new Status(IStatus.INFO, Mevenide.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				int val = DebugUITools.openLaunchConfigurationDialog(shell, configuration, IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP, null);
				if ( val == Window.CANCEL ) {
					try {
						configuration.delete();
					}
					catch ( Exception e ) {
						log.debug("Exception while cancelling launch : ", e ); //$NON-NLS-1$
					}
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
}
