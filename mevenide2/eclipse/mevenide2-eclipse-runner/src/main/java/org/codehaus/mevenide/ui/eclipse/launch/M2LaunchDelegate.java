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
package org.codehaus.mevenide.ui.eclipse.launch;

import java.util.Map;
import org.codehaus.mevenide.ui.eclipse.ErrorHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.program.launchConfigurations.BackgroundResourceRefresher;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class M2LaunchDelegate extends LaunchConfigurationDelegate {
    
    
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        IPath workingDirectory = ExternalToolsUtil.getWorkingDirectory(configuration);
        String basedir = null;
		if (workingDirectory != null) {
			basedir= workingDirectory.toOSString();
		}
        
        monitor.worked(1);
		
        run(configuration, launch, monitor, basedir, null);
    }
    
    public void run(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor, String basedir, Map attributes) throws CoreException {
     
	    final M2Process process = new M2Process(basedir, launch, attributes);
		
		final M2Runner runner= new M2Runner();
		runner.setConfiguration(configuration);

		
		
		if (CommonTab.isLaunchInBackground(configuration)) {
			Runnable r = new Runnable() {
				public void run() {
					try {
						runner.run(process);
						DebugUIPlugin.getDefault().getConsoleDocumentManager().launchAdded(process.getLaunch());
					} 
					catch (Exception e) {
						ErrorHandler.handleException(LaunchMessages.instance().getString("M2LaunchDelegate.Failure"), e); //$NON-NLS-1$
					}
					process.terminated();
				}
			};
			Thread background = new Thread(r);
			background.start();
			monitor.worked(1);
			//refresh resources after process finishes
			if (RefreshTab.getRefreshScope(configuration) != null) {
				BackgroundResourceRefresher refresher = new BackgroundResourceRefresher(configuration, process);
				refresher.startBackgroundRefresh();
			}	
		} 
		else {
			//execute the build 
			try {
				runner.run(monitor);
			} 
			catch (Exception e) {
				process.terminated();
				monitor.done();
				ErrorHandler.handleException(LaunchMessages.instance().getString("M2LaunchDelegate.Failure"), e); //$NON-NLS-1$
				return;
			}
			process.terminated();
			
			RefreshTab.refreshResources(configuration, monitor);
		}
    }
    
}
