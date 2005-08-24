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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.ui.RefreshTab;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenLaunchesListener implements ILaunchesListener2 {
    
    
    private static final Log log = LogFactory.getLog(MavenLaunchesListener.class);
    
    public void launchesAdded(ILaunch[] launches) { }
    public void launchesChanged(ILaunch[] launches) { }
    public void launchesRemoved(ILaunch[] launches) { }
    
    public void launchesTerminated(ILaunch[] launches) {
        for (int i = 0; i < launches.length; i++) {
	        ILaunch launch = launches[i];
	        if ( MavenLaunchDelegate.isMavenLaunch(launch) ) {
	            try {
	                RefreshTab.refreshResources(launch.getLaunchConfiguration(), new NullProgressMonitor());
                }
                catch (CoreException e) {
                    String message = "Cannot refresh resources";  //$NON-NLS-1$
                    log.error(message, e);
                }
	        }
        }
    }
}
