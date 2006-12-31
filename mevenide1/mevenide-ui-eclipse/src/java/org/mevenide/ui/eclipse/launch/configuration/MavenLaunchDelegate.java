/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.JavaLocalApplicationLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.runner.AbstractRunner;
import org.mevenide.runner.ArgumentsManager;
import org.mevenide.runner.RunnerHelper;
import org.mevenide.runner.RunnerUtils;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.preferences.dynamic.DynamicPreferencesManager;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * 
 * @todo refactor-me so that MavenLaunchDelegate doesnot extend AbstractRunner but rather AbstractLaunchConfigurationDelegate rather 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenLaunchDelegate extends AbstractRunner implements ILaunchConfigurationDelegate {
	JavaLocalApplicationLaunchConfigurationDelegate a;
	AbstractJavaLaunchConfigurationDelegate         b;

	private static Log log = LogFactory.getLog(MavenLaunchDelegate.class); 
	
    private static final String FOREHEAD_LIBRARY = "lib/forehead-1.0-beta-5.jar"; //$NON-NLS-1$
	public static final String MAVEN_LAUNCH = "org.mevenide.maven.launched"; //$NON-NLS-1$

	private ILaunchConfiguration config;

	
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		config = configuration;
		
		try {
			initEnvironment();
		} 
		catch (Exception e) {
			log.debug("Unable to launch configuration due to : ", e); //$NON-NLS-1$
		}
		
		assertRequiredLocationsConfigured();
		
		String[] mavenClasspath = ArgumentsManager.getMavenClasspath();

		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("com.werken.forehead.Forehead", mavenClasspath); //$NON-NLS-1$
		/*
		 * ArgumentsManager uses ConfigUtils to set the maximum heap (-Xmx).
		 * We must, therefore, set the value before calling ArgumentsManager.
		 */
		IPersistentPreferenceStore preferences = Mevenide.getInstance().getCustomPreferenceStore();
		int heapSize = preferences.getInt(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY);
	    ConfigUtils.setHeapSize(heapSize);

		String[] vmArgs = ArgumentsManager.getVMArgs(this);
		
		Map customVmArgsMap = configuration.getAttribute(MavenArgumentsTab.SYS_PROPERTIES, new HashMap());
		String[] customVmArgs = new String[customVmArgsMap.size()]; 
		
		Iterator iterator = customVmArgsMap.keySet().iterator();
		int u = 0;
		while ( iterator.hasNext() ) {
			String prop = (String) iterator.next();
			String value = (String) customVmArgsMap.get(prop);
			customVmArgs[u] = "-D" + prop + "=" + value;  //$NON-NLS-1$//$NON-NLS-2$
			u++; 
		}
		
		String[] allVmArgs = new String[vmArgs.length + customVmArgs.length + 1];
		System.arraycopy(vmArgs, 0, allVmArgs, 0, vmArgs.length);
		System.arraycopy(customVmArgs, 0, allVmArgs, vmArgs.length, customVmArgs.length);
		
		String toolsJarArg = org.mevenide.util.StringUtils.isNull(RunnerUtils.getToolsJar(this)) ? 
		        				RunnerUtils.getToolsJar() : RunnerUtils.getToolsJar(this); //$NON-NLS-1$
		if ( !org.mevenide.util.StringUtils.isNull(toolsJarArg) ) {
			allVmArgs[allVmArgs.length - 1] = "-Dtools.jar=" + toolsJarArg; //$NON-NLS-1$
		}
		else {
		    IStatus status = new Status(IStatus.ERROR, "mevenide", 1, "File tools.jar (classes.jar) cannot be found. Please set it in the preference pages.", null); //$NON-NLS-1$ //$NON-NLS-2$
		    throw new CoreException(status);
		}
		
		if ( log.isDebugEnabled() ) {
			for (int i = 0; i < allVmArgs.length; i++) {
	            log.debug("VM Argument : " + allVmArgs[i]); //$NON-NLS-1$
	        }
		}
		
		vmConfig.setVMArguments(allVmArgs);
		
        vmConfig.setProgramArguments( getMavenArgs(getOptions(configuration, Arrays.asList(allVmArgs)), getGoals(configuration) ) );
        vmConfig.setWorkingDirectory(getBasedir());
        
		String launchMode = ILaunchManager.RUN_MODE;
        
        IVMRunner vmRunner = getVMInstall().getVMRunner(launchMode);
		
 		if (vmRunner != null) {
 			vmRunner.run(vmConfig, launch, monitor);
            launch.setAttribute(MAVEN_LAUNCH, "true"); //$NON-NLS-1$
            
            ILaunchesListener2 launchListener = new MavenLaunchesListener();
            DebugPlugin.getDefault().getLaunchManager().addLaunches(new ILaunch[] {launch});
            DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
		}

	}
	
	
	private void assertRequiredLocationsConfigured() throws CoreException {
				
        String javaHome = getJavaHome();
        String mavenHome = Mevenide.getInstance().getCustomPreferenceStore().getString(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY);
        String toolsJarArg = getToolsJar();
        
        boolean mavenHomeDefined = !org.mevenide.util.StringUtils.isNull(mavenHome); 
        boolean javaHomeDefined = !org.mevenide.util.StringUtils.isNull(javaHome);
        boolean toolsJarDefined = !org.mevenide.util.StringUtils.isNull(toolsJarArg);  
        
        if ( !(toolsJarDefined && mavenHomeDefined && javaHomeDefined) ) {
            Status status = new Status(IStatus.ERROR, "mevenide", 0, "Missing required locations. Please use Preference page to set them.", null);
            throw new CoreException(status);
        }
    }

    //this is quicky and should be refactored.. however it could be considered  
	//as a kind of base for the per-project arguments management (or something like that) 
	private String[] getDynamicProperties(ILaunchConfiguration configuration, List vmArgs) {
	    
	    IQueryContext context = null;
	    try {
	        context = getQueryContext(new File(getBasedir()));
        }
        catch (Exception e) {
            String message = "Unable to obtain property resolver"; //$NON-NLS-1$ 
            log.error(message, e);
        }
        
	    List loadedProperties = new ArrayList();
        for (int i = 0; i < vmArgs.size(); i++) {
            String property = (String) vmArgs.get(i); 
            if ( property.indexOf('=') >= 2 ) {
	            property = property.substring(2, property.indexOf('='));
	            loadedProperties.add(property);
            }
        }
        
        //dynamic preferences -- should they instead be merged with customVmArgs ?
        Map dynamicPreferencesMap = DynamicPreferencesManager.getDynamicManager().getPreferences();
        List dynamicPreferencesList = new ArrayList();
        for (Iterator it = dynamicPreferencesMap.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            String value = (String) dynamicPreferencesMap.get(key);
            if ( !loadedProperties.contains(key) && org.mevenide.util.StringUtils.isNull(context.getPropertyValue(key)) ) {
                dynamicPreferencesList.add("-D" + key + "=" + value); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        String[] dynamicPreferences = (String[]) dynamicPreferencesList.toArray(new String[dynamicPreferencesList.size()]);
        return dynamicPreferences;
    }
	
	private IQueryContext getQueryContext(File file) {
	    IQueryContext queryContext = new DefaultQueryContext(file);
	    return queryContext;
    }

    public static boolean isMavenLaunch(ILaunch launch) {
	    return "true".equals(launch.getAttribute(MavenLaunchDelegate.MAVEN_LAUNCH)); //$NON-NLS-1$
	}
	
	public IVMInstall getVMInstall() {
        
        IProject project = FileUtils.getParentProjectForFile(new File(getBasedir()));
        
        try {
            if ( project != null && project.hasNature(JavaCore.NATURE_ID) ) {
                return JavaRuntime.getVMInstall(JavaCore.create(project));
            }
        }
        catch ( Exception e ) {
            log.error(e);
        }
        
		return JavaRuntime.getDefaultVMInstall();
	}


	private String[] getGoals(ILaunchConfiguration configuration) {
		try {
			String strg = configuration.getAttribute(MavenArgumentsTab.GOALS_TO_RUN, ""); //$NON-NLS-1$
			return StringUtils.split(strg, " "); //$NON-NLS-1$
		}	
		catch (CoreException e) {
			log.debug("Unable to get Goals due to : ", e); //$NON-NLS-1$
			return new String[0];
		}		
	}

	private String[] getOptions(ILaunchConfiguration configuration, List vmArgs) {
		try {
		    //static options
			Map map = configuration.getAttribute(MavenArgumentsTab.OPTIONS_MAP, new HashMap());
			log.debug("Found " + map.size() + " options in configuration : ");  //$NON-NLS-1$//$NON-NLS-2$
			List options = new ArrayList();
			Iterator iterator = map.keySet().iterator();
			int idx = 0;
			while (iterator.hasNext()) {
				String strg = (String)iterator.next();
				Character element = new Character(strg.charAt(0));
				if ( Boolean.valueOf((String)map.get(strg)).booleanValue() )  {
					options.add("-" + element); //$NON-NLS-1$
					idx++;
					log.debug(strg + " => " + map.get(strg)); //$NON-NLS-1$
				}
			}
			String[] result = (String[]) options.toArray(new String[options.size()]);
			
			String[] dynamicPreferences = getDynamicProperties(configuration, vmArgs); 
                    
			//merge various sources
			String[] mergedOptions = new String[result.length + dynamicPreferences.length];
			System.arraycopy(dynamicPreferences, 0, mergedOptions, 0, dynamicPreferences.length);
			System.arraycopy(result, 0, mergedOptions, dynamicPreferences.length, result.length);
			
			if ( log.isDebugEnabled() ) {
				log.debug("options passed to Maven " + mergedOptions.length); //$NON-NLS-1$
				for (int i = 0; i < mergedOptions.length; i++) {
					log.debug("\t" + mergedOptions[i]);        //$NON-NLS-1$
	            }
			}
			
			return mergedOptions;
		} 
		catch (CoreException e) {
			log.debug("Unable to get Options due to : ", e); //$NON-NLS-1$
			return new String[0];
		}
	}
	


    protected String getBasedir() {
        try {
			return ExternalToolsUtil.getWorkingDirectory(config).toOSString();
		} catch (Exception e) {
			log.debug("Unable to obtain basedir due to : " + e + " ; returning : Mevenide.getPlugin().getCurrentDir()");  //$NON-NLS-1$//$NON-NLS-2$

            ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
            ISelection selection = selectionService.getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection ss = (IStructuredSelection)selection;
                Object firstElement = ss.getFirstElement();
                if (firstElement instanceof IJavaProject) {
                    firstElement = ((IJavaProject)firstElement).getProject();
                }
                if (firstElement instanceof IResource) {
                    IResource resource = (IResource)firstElement;
                    IProject project = resource.getProject();
                    if (project != null) {
                        return project.getLocation().toOSString();
                    }
                }
            }

            return ""; //$NON-NLS-1$
		}
	}

	protected void initEnvironment() throws Exception  {
        RunnerHelper.setHelper(new RunnerHelper() {
            private String foreHead = null;
        
            public String getForeheadLibrary() {
                if (this.foreHead == null) {
                    final URL rootURL = Mevenide.getInstance().getBundle().getEntry("/"); //$NON-NLS-1$
                    try {
                        URL foreHeadURL = new URL(Platform.resolve(rootURL), FOREHEAD_LIBRARY);
                        this.foreHead = foreHeadURL.getFile();
                    } catch (IOException e) {
                        final String msg = "Unable to resolve path to the Forehead library at '" + FOREHEAD_LIBRARY + "'."; //$NON-NLS-1$//$NON-NLS-2$
                        Mevenide.displayError(msg, e);
                        this.foreHead = rootURL.getFile();
                    }
        
                }
                return this.foreHead;
            }
        });
	}

	protected void launchVM(String[] options, String[] goals) throws Exception {
		throw new RuntimeException("Altho this class uses facilities offered by AbstractRunner. It is not meant to be run like that. TODO : refactor me."); //$NON-NLS-1$
	}

	public String getJavaHome() {
		return Mevenide.getInstance().getCustomPreferenceStore().getString(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY);
	}

	public String getMavenHome() {
        return getValue(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY);
	}

    private String getValue(String key) {
        String res = null;
        
        IProject project = FileUtils.getParentProjectForFile(new File(getBasedir()));
        IFile props = project.getFile(".settings/org.mevenide.ui.prefs");
        
        try {
            if ( props.exists() ) {
                Properties properties = new Properties(); 
                properties.load(props.getContents(true));
                res = properties.getProperty(key);
            }
            if ( StringUtils.isEmpty(res) ) {
                res = Mevenide.getInstance().getCustomPreferenceStore().getString(key); 
            }
        } 
        catch (Exception e) {
            log.debug("Problem while loading project overriden properties");
        }
        return res;
    }

	public String getMavenLocalHome() {
        return getValue(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY);
	}

	public String getMavenLocalRepository() {
        return getValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY);
	}

	public String getToolsJar() {
        String toolsJar = getValue(MevenidePreferenceKeys.TOOLS_JAR_PREFERENCE_KEY);
		toolsJar = StringUtils.isEmpty(toolsJar) ? RunnerUtils.getToolsJar() : toolsJar;
		return toolsJar;
	}

}