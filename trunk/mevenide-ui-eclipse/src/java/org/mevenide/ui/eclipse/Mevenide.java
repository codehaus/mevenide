/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
import org.eclipse.core.runtime.IPluginDescriptor;
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
import org.mevenide.Environment;
import org.mevenide.runner.RunnerHelper;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo move preferences related fields outside of Mevenide class 
 *  
 */
public class Mevenide extends AbstractUIPlugin {
	
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


    /// initialization methods ---
	public Mevenide(IPluginDescriptor descriptor) throws Exception {
		
		super(descriptor);
		try {
			plugin = this;
			NATURE_ID = Mevenide.getResourceString("maven.nature.id");
            
			loadPreferences();
            
		} 
		catch (Exception x) {
			log.debug("Mevenide couldnot initialize due to : " + x);
			throw x;
		}
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
    public static Mevenide getPlugin() {
        return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = Mevenide.getPlugin().getResourceBundle();
		try {
			return bundle.getString(key);
		} 
		catch (MissingResourceException e) {
			log.debug("Cannot find Bundle Key '" + key + "' due to : " + e);
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
            URL installURL = Mevenide.getPlugin().getDescriptor().getInstallURL();
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
		File baseDir = Mevenide.getPlugin().getStateLocation().toFile();
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
        	URL installBase = Mevenide.getPlugin().getDescriptor().getInstallURL();
        	return new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath()).toString();
        }
        catch (IOException e) {
            log.debug("Unable to locate local repository due to " + e);
            return "";
        }
    }
    
    public String getForeheadConf() {
        try {
            URL installBase = Mevenide.getPlugin().getDescriptor().getInstallURL();
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
		Environment.setMavenHome(getMavenHome()); 
		Environment.setJavaHome(getJavaHome());
		Environment.setMavenLocalRepository(getMavenRepository());
		Environment.setMavenPluginsInstallDir(getPluginsInstallDir());
		Environment.setMavenLocalHome(getMavenLocalHome());
		Environment.setHeapSize(getHeapSize());
		RunnerHelper.setHelper(
			new RunnerHelper() {
				private String foreHead = null;
				public String getForeheadLibrary() {
					if ( foreHead == null ) {
						try {
							URL foreHeadURL = new URL(Platform.resolve(Mevenide.getPlugin().getDescriptor().getInstallURL()), "lib/" + Mevenide.getResourceString("forehead.library"));
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
		Mevenide.getPlugin().createProjectProperties();
		
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
		Environment.setJavaHome(javaHome);
    }
    public String getMavenHome() {
        return mavenHome;
    }
    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
		Environment.setMavenHome(mavenHome);
    }
	public String getMavenRepository() {
		return mavenRepository;
	}
	public void setMavenRepository(String mavenRepository) {
		this.mavenRepository = mavenRepository;
		Environment.setMavenLocalRepository(mavenRepository);
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
	public File getPom() {
		return new File(new DefaultPathResolver().getAbsolutePath(project.getFile("project.xml").getLocation()));
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
        Environment.setMavenLocalHome(mavenLocalHome);
    }
    public int getHeapSize() {
        return heapSize;
    }
    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
        Environment.setHeapSize(heapSize);
    }

}
