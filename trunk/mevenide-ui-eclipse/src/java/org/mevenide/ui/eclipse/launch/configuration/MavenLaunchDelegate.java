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


/**
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
			String strg = (String) configuration.getAttribute(MavenArgumentsTab.GOALS_TO_RUN, "");
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