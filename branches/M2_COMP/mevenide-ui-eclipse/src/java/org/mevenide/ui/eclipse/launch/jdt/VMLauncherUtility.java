/*
 * (c) Copyright 2002 Thomas Papiernik - Cross Systems.
 * 
 * Licensed under CPL 1.0
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 */
package org.mevenide.ui.eclipse.launch.jdt;


import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author Thomas Papiernik
 * @author Gilles Dodinet
 */
public class VMLauncherUtility {
	private VMLauncherUtility() { 
	}
	
	
  
	static public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
	}

    /**
     * run a class in ILaunchManager.RUN_MODE mode
     * 
     * Mevenide specific
     * 
     * @param classToLaunch
     * @param classPath
     * @param vmArgs
     * @param prgArgs
     * 
     * @throws Exception
     */
    public static void runVM(String classToLaunch,
                    		 String[] classPath,
                    		 String[] vmArgs,
                             String[] prgArgs) throws Exception {
        
        
        String launchMode = ILaunchManager.RUN_MODE;
        
        IVMRunner vmRunner = getVMInstall().getVMRunner(launchMode);
        
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType[] launchTypes = manager.getLaunchConfigurationTypes();
		ILaunchConfiguration config = launchTypes[0].newInstance(null, Mevenide.getResourceString("VMLauncherUtility.launch.name"));
        
        ILaunch launch = new Launch(config, launchMode, null);
        
        
        VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(classToLaunch, classPath);
		vmConfig.setVMArguments(vmArgs);
        vmConfig.setProgramArguments(prgArgs);
        
        if (vmRunner != null) {
            vmRunner.run(vmConfig, launch, null);
            DebugUIPlugin.getDefault().getConsoleDocumentManager().launchAdded(launch);
		}
	}

}