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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.mevenide.runner.AbstractRunner;
import org.mevenide.runner.ArgumentsManager;
import org.mevenide.ui.eclipse.Mevenide;


/**
 * 
 * @todo refactor-me so that MavenLaunchDelegate doesnot extend AbstractRunner but rather AbstractLaunchConfigurationDelegate rather 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenLaunchDelegate extends AbstractRunner implements ILaunchConfigurationDelegate {
	private static Log log = LogFactory.getLog(MavenLaunchDelegate.class); 
	
	private ILaunchConfiguration config;
	
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		config = configuration;
		try {
			initEnvironment();
		} 
		catch (Exception e) {
			log.debug("Unable to launch configuration due to : ", e);
		}
	
		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("com.werken.forehead.Forehead", ArgumentsManager.getMavenClasspath());
		String[] vmArgs = ArgumentsManager.getVMArgs(this);
		
		Map customVmArgsMap = configuration.getAttribute(MavenArgumentsTab.SYS_PROPERTIES, new HashMap());
		String[] customVmArgs = new String[customVmArgsMap.size()]; 
		
		Iterator iterator = customVmArgsMap.keySet().iterator();
		int u = 0;
		while ( iterator.hasNext() ) {
			String prop = (String) iterator.next();
			String value = (String) customVmArgsMap.get(prop);
			customVmArgs[u] = "-D" + prop + "=" + value;
			u++; 
		}
		
		String[] allVmArgs = new String[vmArgs.length + customVmArgs.length];
		System.arraycopy(vmArgs, 0, allVmArgs, 0, vmArgs.length);
		System.arraycopy(customVmArgs, 0, allVmArgs, vmArgs.length, customVmArgs.length);
		
		for (int i = 0; i < allVmArgs.length; i++) {
            log.debug("VM Argument : " + allVmArgs[i]);
        }
		
		vmConfig.setVMArguments(allVmArgs);
		
        vmConfig.setProgramArguments( getMavenArgs(getOptions(configuration), getGoals(configuration) ) );
        vmConfig.setWorkingDirectory(getBasedir());
        
		String launchMode = ILaunchManager.RUN_MODE;
        
        IVMRunner vmRunner = getVMInstall().getVMRunner(launchMode);
		
 		if (vmRunner != null) {
 			//System.err.println(getBasedir() + " => " + getGoals(configuration));
            vmRunner.run(vmConfig, launch, monitor);
            DebugUIPlugin.getDefault().getConsoleDocumentManager().launchAdded(launch);
		}

	}
	

	public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
	}


	private String[] getGoals(ILaunchConfiguration configuration) {
		try {
			String strg = (String) configuration.getAttribute(MavenArgumentsTab.GOALS_TO_RUN, "");
			return StringUtils.split(strg, " ");
		}	
		catch (CoreException e) {
			log.debug("Unable to get Goals due to : ", e);
			return new String[0];
		}		
	}

	private String[] getOptions(ILaunchConfiguration configuration) {
		try {
			Map map = (Map) configuration.getAttribute(MavenArgumentsTab.OPTIONS_MAP, new HashMap());
			log.debug("Found " + map.size() + " options in configuration : ");
			String[] options = new String[map.size()];
			Iterator iterator = map.keySet().iterator();
			int idx = 0;
			while (iterator.hasNext()) {
				String strg = (String)iterator.next();
				Character element = new Character(strg.charAt(0));
				if ( Boolean.valueOf((String)map.get(strg)).booleanValue() )  {
					options[idx] = "-" + element;
					idx++;
					log.debug(strg + " => " + map.get(strg));
				}
			}
			String[] result = new String[idx];
			System.arraycopy(options, 0, result, 0, idx);
			log.debug("options passed to Maven " + result.length);
			return result;
		} 
		catch (CoreException e) {
			log.debug("Unable to get Options due to : ", e);
			return new String[0];
		}
	}
	

	protected String getBasedir() {
        try {
			//return Mevenide.getPlugin().getCurrentDir();
			log.debug("basedir = " + ExternalToolsUtil.getWorkingDirectory(config).toOSString());
			return ExternalToolsUtil.getWorkingDirectory(config).toOSString();
			
		} 
		catch (Exception e) {
			log.debug("Unable to obtain basedir due to : " + e + " ; returning : Mevenide.getPlugin().getCurrentDir()");
			return Mevenide.getInstance().getCurrentDir();
		}
	}




	protected void initEnvironment() throws Exception  {
		if ( Mevenide.getInstance().getMavenHome() == null || Mevenide.getInstance().getMavenHome().trim().equals("") ) { 
			//noMavenHome();
			//throw new Exception("Maven Home has not been set");
	    }
	    else {
			Mevenide.getInstance().initEnvironment();
	    }
	}

	private void noMavenHome() {
	    //EnvironmentUtil.loadEnvironment();
   }

	protected void launchVM(String[] options, String[] goals) throws Exception {
		throw new RuntimeException("Altho this class uses facilities offered by AbstractRunner. It is not meant to be run like that. TODO : refactor me.");
	}
}