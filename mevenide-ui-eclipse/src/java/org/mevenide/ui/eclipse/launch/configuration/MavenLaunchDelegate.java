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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
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
import org.mevenide.ui.eclipse.preferences.DynamicPreferencesManager;



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
	
	public static final String MAVEN_LAUNCH = "org.mevenide.maven.launched";

	private ILaunchConfiguration config;

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		config = configuration;
		try {
			initEnvironment();
		} 
		catch (Exception e) {
			log.debug("Unable to launch configuration due to : ", e);
		}
		
		String[] mavenClasspath = ArgumentsManager.getMavenClasspath();

		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("com.werken.forehead.Forehead", mavenClasspath);
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
            launch.setAttribute(MAVEN_LAUNCH, "true");
            
            ILaunchesListener2 launchListener = new MavenLaunchesListener();
            DebugPlugin.getDefault().getLaunchManager().addLaunches(new ILaunch[] {launch});
            DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
            
            DebugUIPlugin.getDefault().getConsoleDocumentManager().launchAdded(launch);
		}

	}
	
	public static boolean isMavenLaunch(ILaunch launch) {
	    return "true".equals(launch.getAttribute(MavenLaunchDelegate.MAVEN_LAUNCH));
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
		    //static options
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
			
			//dynamic preferences
			DynamicPreferencesManager preferencesManager = DynamicPreferencesManager.getDynamicManager();
			preferencesManager.loadPreferences();
			Map dynamicPreferencesMap = preferencesManager.getPreferences();
			String[] dynamicPreferences = new String[dynamicPreferencesMap.size()];
			idx = 0;
			for (Iterator it = dynamicPreferencesMap.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String value = (String) dynamicPreferencesMap.get(key);
                dynamicPreferences[idx] = "-D" + key + "=" + value;
            }
			
			//merge various sources
			String[] mergedOptions = new String[result.length + dynamicPreferences.length];
			System.arraycopy(result, 0, mergedOptions, 0, result.length);
			System.arraycopy(dynamicPreferences, 0, mergedOptions, result.length, dynamicPreferences.length);
			
			log.debug("options passed to Maven " + mergedOptions.length);
			System.err.println("options passed to Maven : " );
			for (int i = 0; i < mergedOptions.length; i++) {
				System.err.println("\t" + mergedOptions[i]);
                
            }
			
			return mergedOptions;
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