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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.runner.RunnerHelper;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.osgi.framework.BundleContext;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo move preferences related fields outside of Mevenide class 
 * @todo get rid of the static method and make use of new bundle capabilities
 *  
 */
public class Mevenide extends AbstractUIPlugin  {
	
	private static Log log = LogFactory.getLog(Mevenide.class);
	 
	private static Mevenide plugin;
	
	private Object lock = new Object();
	
	public static String NATURE_ID ;
	public static String SYNCH_VIEW_ID = "org.mevenide.sync.view"; 
	public static String PLUGIN_ID = "org.mevenide.ui";
	public static String PLUGIN_NAME = "Mevenide";
	
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

    /// initialization methods ---
	public Mevenide() throws Exception {
		try {
			plugin = this;
			NATURE_ID = Mevenide.getResourceString("maven.nature.id");
		} 
		catch (Exception x) {
			log.debug("Mevenide couldnot initialize due to : ", x);
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
        if ( mavenRepo == null || mavenRepo.trim().equals("") ) { 	
        	mavenRepo = new File(mavenLocalHome, "repository").getAbsolutePath();
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
        if ( localHome == null || localHome.trim().equals("") ) {
			localHome = new File(System.getProperty("user.home"), ".maven").getAbsolutePath();
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
			log.error("Cannot find Bundle Key '" + key + "' due to : " + e);
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
					resourceBundle = ResourceBundle.getBundle("MavenPluginResources");
				}
				return resourceBundle;
			}
		}
	}
	
    public static ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = Mevenide.getResourceString("IconsPath");
        try {
            URL installURL = plugin.getBundle().getEntry("/");
            URL url = new URL(installURL, iconPath + "/" + relativePath);
            return ImageDescriptor.createFromURL(url);
        } 
        catch (MalformedURLException e) {
            // should not happen
            log.debug("Cannot find ImageDescriptor for '" + relativePath + "' due to : " + relativePath);
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
    
    
    /// utililty methods ---
    public String getPreferencesFilename() {
		return getFile("prefs.ini");   
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
		IFile props = project.getFile("project.properties");
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
        	URL installBase = getBundle().getEntry("/");
        	return new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath()).toString();
        }
        catch (IOException e) {
            log.debug("Unable to locate local repository due to " + e);
            return "";
        }
    }
    
    public String getForeheadConf() {
        try {
            URL installBase = getBundle().getEntry("/");
            File f = new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath(), "conf.file");
            return f.getAbsolutePath();
        }
        catch (IOException e) {
            log.debug("Unable to locate forehead.conf due to : " + e);
            return "";
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
							URL foreHeadURL = new URL(Platform.resolve(getBundle().getEntry("/")), "lib/" + Mevenide.getResourceString("forehead.library"));
                          	//@todo could cause bug if plugin isnot installed locally. URLs should be resolved in other way
                          	foreHead = foreHeadURL.getFile();
                            log.debug("ForeHead library : " + foreHeadURL);
                        }
                        catch (IOException e) {
                            log.debug("Unable to get forehead lib : ", e);
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
		
		File f = new Path(project.getLocation().append("project.properties").toOSString()).toFile();
		Properties properties = new Properties();
		properties.load(new FileInputStream(f));
	
		IPathResolver resolver = new DefaultPathResolver();
	
		String buildPath = resolver.getRelativePath(project, javaProject.getOutputLocation()); 
		properties.setProperty("maven.build.dest", buildPath);
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
		return defaultGoals != null  && !defaultGoals.trim().equals("") ? defaultGoals : "test";
	}
	public void setDefaultGoals(String defaultGoals) {
		this.defaultGoals = defaultGoals;
	}
    public String getPluginsInstallDir() {
        return new File(mavenLocalHome, "plugins").getAbsolutePath();
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

}
