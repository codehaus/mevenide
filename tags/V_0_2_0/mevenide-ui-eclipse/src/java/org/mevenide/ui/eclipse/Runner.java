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
package org.mevenide.ui.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.mevenide.MevenideException;
import org.mevenide.runner.AbstractRunner;
import org.mevenide.runner.ArgumentsManager;
import org.mevenide.ui.eclipse.launch.jdt.VMLauncherUtility;

//import com.werken.forehead.Forehead;

/**
 * @author Gilles Dodinet  
 * @version $Id$
 */
public class Runner extends AbstractRunner {
	private static final String FOREHEAD_MAIN = "com.werken.forehead.Forehead";
    private static final String MAVEN_HOME_NOT_SET_MESSAGE = "Runner.mavenHome.NotSet.message";
    private static final String MAVEN_HOME_NOT_SET_TITLE = "Runner.mavenHome.NotSet.title";
    
    Mevenide plugin = Mevenide.getInstance();

	public Runner() throws MevenideException {
		super();
	}

    /**
	 * @see org.mevenide.core.AbstractRunner#getEffectiveDirectory()
	 */
	protected String getBasedir() {
        return plugin.getCurrentDir();
	}

	/**
	 * @see org.mevenide.core.AbstractRunner#initEnvironment()
	 */
	protected void initEnvironment() throws Exception  {
		if ( plugin.getMavenHome() == null || plugin.getMavenHome().trim().equals("") ) { 
			MessageBox dialog = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
			dialog.setText (Mevenide.getResourceString(MAVEN_HOME_NOT_SET_TITLE));
			dialog.setMessage (Mevenide.getResourceString(MAVEN_HOME_NOT_SET_MESSAGE));
			dialog.open ();
			throw new Exception("Maven Home has not been set");
	    }
	    else {
		    plugin.initEnvironment();
	    }
	}

	/**
     * @param options
	 * @param goals
	 * @throws Exception
	 */
	protected void launchVM(String[] options, String[] goals) throws Exception {
	
	    VMLauncherUtility.runVM(
			FOREHEAD_MAIN,
			ArgumentsManager.getMavenClasspath(),
		    ArgumentsManager.getVMArgs(this),
	        getMavenArgs(options, goals));
		
	}

}
