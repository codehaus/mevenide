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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.runner.RunnerHelper;
import org.mevenide.ui.eclipse.nature.ActionDefinitionsManager;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.util.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo clean-me move preferences related fields outside of Mevenide class 
 *  
 */
public class Mevenide extends AbstractUIPlugin {
    
    private static Log log = LogFactory.getLog(Mevenide.class);
	 
	private static Mevenide plugin;
	
	//we need it to update the menu correctly in the builder 
	private IWorkbenchWindow lastActiveWindow;
	
    private Object lock = new Object();
	
	public static final String NATURE_ID = "org.mevenide.ui.mavennature"; //$NON-NLS-1$
	public static final String SYNCHRONIZE_VIEW_ID = "org.mevenide.ui.synchronize.view.SynchronizationView"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "org.mevenide.ui"; //$NON-NLS-1$
	public static final String PLUGIN_NAME = "Mevenide"; //$NON-NLS-1$

	private static final String ICONS_PATH = "icons/"; //$NON-NLS-1$
	private static final String LIB_FOLDER = "lib/"; //$NON-NLS-1$
	private static final String FOREHEAD_LIBRARY = "forehead-1.0-beta-5.jar"; //$NON-NLS-1$
	private static final String PROJECT_PROPERTIES_FILE_NAME = "project.properties"; //$NON-NLS-1$
	
	private ResourceBundle resourceBundle;
	
    private String currentDir;
    private IProject project;

	//should be extracted   
	private String mavenHome;
	private String mavenLocalHome;
    private String javaHome;
    private String mavenRepository;
    private int heapSize;
    private String pomTemplate;
    private boolean checkTimestamp;
	private String defaultGoals;
    private CustomLocationFinder customLocationFinder;


    public static final String DEPENDENCY_TYPE_JAR = "jar"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_EJB = "ejb"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_PLUGIN = "plugin"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_ASPECT = "aspect"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_WAR = "war"; //$NON-NLS-1$

    
    private ActionDefinitionsManager actionDefinitionsManager;
    
    public ActionDefinitionsManager getActionDefinitionsManager() {
        if ( actionDefinitionsManager == null ) {
            actionDefinitionsManager = new ActionDefinitionsManager();
        }
        return actionDefinitionsManager;
    }
    
    public static final String[] KNOWN_DEPENDENCY_TYPES = new String[] {
    	DEPENDENCY_TYPE_JAR, 
    	DEPENDENCY_TYPE_EJB, 
    	DEPENDENCY_TYPE_PLUGIN,
    	DEPENDENCY_TYPE_ASPECT,
    	DEPENDENCY_TYPE_WAR
    };

    public static final String MAVEN_MENU_ID = "org.mevenide.maven.menu.id";

    /// initialization methods ---
	public Mevenide() throws Exception {
		try {
			plugin = this;
		} 
		catch (Exception x) {
			log.debug("Mevenide couldnot initialize due to : ", x); //$NON-NLS-1$
			throw x;
		}
	}
	
	/**
	 * osgi startup : initialize resources
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		customLocationFinder = new CustomLocationFinder();
		loadPreferences();
        initEnvironment();
        //DynamicPreferencePageFactory.getFactory().createPages();
        PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
	        public void windowOpened(IWorkbenchWindow window) {
	            lastActiveWindow = window;
	        }   
	        public void windowActivated(IWorkbenchWindow window) {
	            lastActiveWindow = window;
	        } 
	        public void windowClosed(IWorkbenchWindow window) { }
	        public void windowDeactivated(IWorkbenchWindow window) {}
        });
	}

	
    protected void initializeImageRegistry(ImageRegistry reg) {
        for (int i = 0; i < IImageRegistry.IMAGE_KEYS.length; i++) {
            reg.put(IImageRegistry.IMAGE_KEYS[i], getImageDescriptor(IImageRegistry.IMAGE_KEYS[i]));  
        }
    }
    
    
    
    /**
     * @warn we cast here because we need a WorkbenchWindow to access the MenuManager
     *       however WorkbenchWindow is part of internal api
     */
    public WorkbenchWindow  getWorkbenchWindow() {
        return (WorkbenchWindow) lastActiveWindow;
    } 
    
    /**
	 * osgi shutdown : dispose resources
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
    private void loadPreferences() throws IOException {
        if ( !new File(getPreferencesFilename()).exists() ) {
        	new File(getPreferencesFilename()).createNewFile();
        }
        PreferenceStore preferenceStore = new PreferenceStore(getPreferencesFilename());
        
        preferenceStore.load();
        
        //required preferences
        setMavenHome(preferenceStore.getString(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY));
        setJavaHome(preferenceStore.getString(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY));
        
		//defaulted preferences
        loadMavenLocalHome(preferenceStore);
        loadMavenRepo(preferenceStore);
        loadHeapSize(preferenceStore);
        
        //optional preferences
        setPomTemplate(preferenceStore.getString(MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY));
        setCheckTimestamp(preferenceStore.getBoolean(MevenidePreferenceKeys.MEVENIDE_CHECKTIMESTAMP_PREFERENCE_KEY));
        setDefaultGoals(preferenceStore.getString(MevenidePreferenceKeys.MAVEN_LAUNCH_DEFAULTGOALS_PREFERENCE_KEY));
    }

    private void loadHeapSize(PreferenceStore preferenceStore) {
        int hSize = preferenceStore.getInt(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY);
        //heap has been initialized yet. set it to default (160)
        if ( hSize == 0 ) {
            hSize = 160;
        }
        setHeapSize(hSize);
    }

    private void loadMavenRepo(PreferenceStore preferenceStore) {
        String mavenRepo = preferenceStore.getString(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY);
        //maven.repo has not been initialized - defaults to ${maven.local.home}/repository
        if ( StringUtils.isNull(mavenRepo) ) { 	
        	mavenRepo = new File(mavenLocalHome, "repository").getAbsolutePath(); //$NON-NLS-1$
        }
        setMavenRepository(mavenRepo);
    }

	/**
	 * mavenRepo defaults to ${maven.local.home}/repository 
     * so mavenLocalHome should have been loaded before 
     *
	 * @param preferenceStore
	 * @return
	 */
    private String loadMavenLocalHome(PreferenceStore preferenceStore) {
        //preferences that are defaulted
        String localHome = preferenceStore.getString(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY);
        //maven.local.home has not been initialized - defaults to ${user.home}/.maven
        if ( StringUtils.isNull(localHome) ) {
			localHome = new File(System.getProperty("user.home"), ".maven").getAbsolutePath();  //$NON-NLS-1$//$NON-NLS-2$
        }
        setMavenLocalHome(localHome);
        
        return localHome;
    }

	/// usual Plugin methods ---  
    public static Mevenide getInstance() {
        return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = Mevenide.getInstance().getResourceBundle();
		try {
			return bundle.getString(key);
		} 
		catch (MissingResourceException e) {
			log.error("Cannot find Bundle Key '" + key + "'", e);  //$NON-NLS-1$//$NON-NLS-2$
			return key;
		}
	}

	public static String getResourceString(String key, String param) {
		return getResourceString(key, new String[] {param});
	}

	public static String getResourceString(String key, String[] params) {
		return MessageFormat.format(getResourceString(key), params);
	}

	public ResourceBundle getResourceBundle() {
		if ( resourceBundle != null) {
			return resourceBundle;
		}
		else {
			synchronized (lock) {
				if ( resourceBundle == null ) {
					resourceBundle = ResourceBundle.getBundle("MavenPluginResources"); //$NON-NLS-1$
				}
				return resourceBundle;
			}
		}
	}
	
    private ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = ICONS_PATH;
        try {
            URL installURL = plugin.getBundle().getEntry("/"); //$NON-NLS-1$
            URL url = new URL(installURL, iconPath + "/" + relativePath); //$NON-NLS-1$
            return ImageDescriptor.createFromURL(url);
        } 
        catch (MalformedURLException e) {
            // should not happen
            log.debug("Cannot find ImageDescriptor for '" + relativePath + "' due to : " + relativePath); //$NON-NLS-1$ //$NON-NLS-2$
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
    
    
    /// utililty methods ---
    public String getPreferencesFilename() {
		return getFile("prefs.ini");    //$NON-NLS-1$
    }
    
    public String getDynamicPreferencesFilename() {
		return getFile("dyn_prefs.ini");    //$NON-NLS-1$
    }
    
	public String getFile(String fname) {
		File baseDir = Mevenide.getInstance().getStateLocation().toFile();
		File f = new File(baseDir, fname);
		return f.getAbsolutePath();
	}
    
	/**
	 * create a new POM skeleton if no project.xml currently exists
	 * 
	* @throws Exception
	 */
	public void createPom() throws Exception {
		FileUtils.createPom(project);
	}
	
	public void createProjectProperties() throws Exception {
		IFile props = project.getFile(PROJECT_PROPERTIES_FILE_NAME);
		if ( !new File(props.getLocation().toOSString()).exists() ) {
			props.create(new ByteArrayInputStream(new byte[0]), false, null);
		}
	}
	
	public static void popUp(String text, String message) {
		MessageBox dialog = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
		dialog.setText (text);
		dialog.setMessage (message);
		dialog.open ();
	}
    public String getEffectiveDirectory() {
        try {
        	URL installBase = getBundle().getEntry("/"); //$NON-NLS-1$
        	return new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath()).toString();
        }
        catch (IOException e) {
            log.debug("Unable to locate local repository due to " + e); //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
    }
    
    public String getForeheadConf() {
        try {
            URL installBase = getBundle().getEntry("/"); //$NON-NLS-1$
            File f = new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath(), "conf.file"); //$NON-NLS-1$
            return f.getAbsolutePath();
        }
        catch (IOException e) {
            log.debug("Unable to locate forehead.conf due to", e); //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
    }

	/* {non-javadoc}
	 * 
	 * should not be necessary since the setters already take care of configuring the environment.
	 */
	public void initEnvironment() {
        customLocationFinder.setJavaHome(getJavaHome());
        customLocationFinder.setMavenHome(getMavenHome());
        customLocationFinder.setMavenLocalRepository(getMavenRepository());
        customLocationFinder.setMavenPluginsDir(getPluginsInstallDir());
		customLocationFinder.setMavenLocalHome(getMavenLocalHome());
        ((LocationFinderAggregator)ConfigUtils.getDefaultLocationFinder()).setCustomLocationFinder(customLocationFinder);
//		TODO Milos: what to do with HeapSize, not in ILocationFinder..
//		Environment.setHeapSize(getHeapSize());
		RunnerHelper.setHelper(
			new RunnerHelper() {
				private String foreHead = null;
				public String getForeheadLibrary() {
					if ( foreHead == null ) {
						try {
							URL foreHeadURL = new URL(Platform.resolve(getBundle().getEntry("/")), LIB_FOLDER + FOREHEAD_LIBRARY); //$NON-NLS-1$
                          	foreHead = foreHeadURL.getFile();
                            log.debug("ForeHead library : " + foreHeadURL); //$NON-NLS-1$
                        }
                        catch (IOException e) {
                            log.debug("Unable to get forehead lib : ", e); //$NON-NLS-1$
                        }
                        
					}
				    return foreHead;
                }	
			}
		);
	}

	public void setBuildPath() throws Exception {
		Mevenide.getInstance().createProjectProperties();
		
		IJavaProject javaProject = JavaCore.create(project);
		
		if ( !javaProject.exists() ) {
			return;
		}
		
		File f = new Path(project.getLocation().append(PROJECT_PROPERTIES_FILE_NAME).toOSString()).toFile();
		Properties properties = new Properties();
		properties.load(new FileInputStream(f));
	
		IPathResolver resolver = new DefaultPathResolver();
	
		String buildPath = resolver.getRelativePath(project, javaProject.getOutputLocation()); 
		properties.setProperty("maven.build.dest", buildPath); //$NON-NLS-1$
		properties.store(new FileOutputStream(f), null);
		
		initEnvironment();
	}


    /// setter/getter methods ---
    public String getJavaHome() {
        return javaHome;
    }
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
		customLocationFinder.setJavaHome(javaHome);
    }
    public String getMavenHome() {
        return mavenHome;
    }
    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
		customLocationFinder.setMavenHome(mavenHome);
    }
	public String getMavenRepository() {
		return mavenRepository;
	}
	public void setMavenRepository(String mavenRepository) {
		this.mavenRepository = mavenRepository;
		customLocationFinder.setMavenLocalRepository(mavenRepository);
	}
	public String getCurrentDir() {
		return currentDir;
	}
    public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}

	public void setProject(IProject project) {
		this.project = project;
		this.currentDir = project.getLocation().toOSString();
	}

	public boolean getCheckTimestamp() {
		return checkTimestamp;
	}
	public void setCheckTimestamp(boolean b) {
		checkTimestamp = b;
	}
	public String getPomTemplate() {
		return pomTemplate;
	}
	public void setPomTemplate(String string) {
		pomTemplate = string;
	}
	public String getDefaultGoals() {
		return defaultGoals != null  && !defaultGoals.trim().equals("") ? defaultGoals : "test";  //$NON-NLS-1$//$NON-NLS-2$
	}
	public void setDefaultGoals(String defaultGoals) {
		this.defaultGoals = defaultGoals;
	}
    public String getPluginsInstallDir() {
        return new File(mavenLocalHome, "cache").getAbsolutePath(); //$NON-NLS-1$
    }
    public String getMavenLocalHome() {
        return mavenLocalHome;
    }
    public void setMavenLocalHome(String mavenLocalHome) {
        this.mavenLocalHome = mavenLocalHome;
        customLocationFinder.setMavenLocalHome(mavenLocalHome);
    }
    public int getHeapSize() {
        return heapSize;
    }
    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
//TODO milos: for now just ignoring.. should be sufficient to have in local var..        
//        Environment.setHeapSize(heapSize);
    }
    
    
    public static final PluginVersionIdentifier getEclipseFormsVersion() {
        PluginVersionIdentifier result = null;

        final Bundle formsBundle = Platform.getBundle("org.eclipse.ui.forms");
        if (formsBundle != null) {
            String version = (String)formsBundle.getHeaders().get(Constants.BUNDLE_VERSION);
            IStatus status = PluginVersionIdentifier.validateVersion(version);
            if (status.isOK()) {
                result = new PluginVersionIdentifier(version);
            }
            else {
                getInstance().getLog().log(status);
            }
        }

        return result;
    }

    public static final PluginVersionIdentifier ECLIPSE_FORMS_3_0_0 = new PluginVersionIdentifier(3, 0, 0);
    public static final PluginVersionIdentifier ECLIPSE_FORMS_3_1_0 = new PluginVersionIdentifier(3, 1, 0);
}
