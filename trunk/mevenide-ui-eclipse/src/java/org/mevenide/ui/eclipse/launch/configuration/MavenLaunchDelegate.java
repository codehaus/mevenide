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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.mevenide.core.AbstractRunner;
import org.mevenide.core.ArgumentsManager;
import org.mevenide.ui.eclipse.Mevenide;



public class MavenLaunchDelegate extends AbstractRunner implements ILaunchConfigurationDelegate {
	private static Log log = LogFactory.getLog(MavenLaunchDelegate.class); 
	
	private ILaunchConfiguration config;
	
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		config = configuration;
		try {
			initEnvironment();
		} 
		catch (Exception e) {
			// @todo Auto-generated catch block
			e.printStackTrace();
		}
	
		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("com.werken.forehead.Forehead", ArgumentsManager.getMavenClasspath());
		vmConfig.setVMArguments(ArgumentsManager.getVMArgs(this));
		
        vmConfig.setProgramArguments( getMavenArgs(getOptions(configuration), getGoals(configuration) ) );
        
		String launchMode = ILaunchManager.RUN_MODE;
        
        IVMRunner vmRunner = getVMInstall().getVMRunner(launchMode);

 		if (vmRunner != null) {
            vmRunner.run(vmConfig, launch, monitor);
            DebugUIPlugin.getDefault().getConsoleDocumentManager().launchAdded(launch);
		}

	}
	

	public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
	}


	private String[] getGoals(ILaunchConfiguration configuration) {
		try {
			String strg = (String) configuration.getAttribute(MavenOptionsTab.GOALS_TO_RUN, "");
			return StringUtils.split(strg, " ");
		}	
		catch (CoreException e) {
			e.printStackTrace();
			//@todo temp return value 
			return new String[0];
		}		
	}

	private String[] getOptions(ILaunchConfiguration configuration) {
		try {
			Map map = (Map) configuration.getAttribute(MavenOptionsTab.OPTIONS_MAP, new HashMap());
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
			e.printStackTrace();
			//@todo temp return value 
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
			return Mevenide.getPlugin().getCurrentDir();
		}
	}




	protected void initEnvironment() throws Exception  {
		if ( Mevenide.getPlugin().getMavenHome() == null 
		     || Mevenide.getPlugin().getMavenHome().trim().equals("") ) { 
			noMavenHome();
			throw new Exception("Maven Home has not been set");
	    }
	    else {
			//just a test
			System.getProperty("user.dir");
		    Mevenide.getPlugin().initEnvironment();
	    }
	}

	private void noMavenHome() {
		MessageBox dialog = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
		dialog.setText ("Unset Property : Maven Home");
		dialog.setMessage ("Cannot run Maven unless you set Maven Home. Please see Windows > Preferences > Maven");
		dialog.open ();
   }

	protected void launchVM(String[] options, String[] goals) throws Exception {
		throw new RuntimeException("Altho this class uses facilities offered by AbstractRunner. It is not meant to be run like that. TODO : refactor me.");
	}
}