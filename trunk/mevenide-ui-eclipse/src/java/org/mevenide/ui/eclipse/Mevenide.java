/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */

package org.mevenide.ui.eclipse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mevenide.Environment;
import org.mevenide.project.io.ProjectSkeleton;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @refactor EXTRACTME lots of non related utility methods
 *  
 */
public class Mevenide extends AbstractUIPlugin {
	private static Log log = LogFactory.getLog(Mevenide.class);
	 
	private static Mevenide plugin;
	
	private Object lock = new Object();
	
	public static String NATURE_ID ;
	
	public static String SYNCH_VIEW_ID = "org.mevenide.sync.view"; 
	
	private ResourceBundle resourceBundle;
	
	private String mavenHome;
    private String javaHome;
    private String mavenRepository;
    
    private String currentDir;
    private IProject project;
    
    public Mevenide(IPluginDescriptor descriptor) throws Exception {
		super(descriptor);
		try {
			plugin = this;
			
			NATURE_ID = Mevenide.getResourceString("maven.nature.id");
            
			if ( !new File(getPreferencesFilename()).exists() ) {
            	new File(getPreferencesFilename()).createNewFile();
			}
            PreferenceStore preferenceStore = new PreferenceStore(getPreferencesFilename());
            
            preferenceStore.load();
            
            setMavenHome(preferenceStore.getString("maven.home"));
            setJavaHome(preferenceStore.getString("java.home"));
            setMavenRepository(preferenceStore.getString("maven.repo"));
            
            
            
            
		} 
		catch (Exception x) {
			log.debug("Mevenide couldnot initialize due to : " + x);
			throw x;
		}
	}

    /** 
     * @return MavenPlugin
     */
	public static Mevenide getPlugin() {
        return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
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
    
    
    public String getJavaHome() {
        return javaHome;
    }

    public String getMavenHome() {
        return mavenHome;
    }

	public String getMavenRepository() {
		return mavenRepository;
	}
		
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
		Environment.setJavaHome(javaHome);
    }

    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
		Environment.setMavenHome(mavenHome);
    }
    
	public void setMavenRepository(String mavenRepository) {
		this.mavenRepository = mavenRepository;
		Environment.setMavenRepository(mavenRepository);
	}
	
    public String getPreferencesFilename() {
		return getFile("prefs.ini");   
    }
	
	public String getGoalsPreferencesFilename() {
		return getFile("goals_prefs.ini");
	}
	
	public String getFile(String fname) {
		File baseDir = Mevenide.getPlugin().getStateLocation().toFile();
		
		File f = new File(baseDir, fname);
		return f.getAbsolutePath();

	}
	
    public String getXmlGoalsFile() {
        return getFile("maven-goals.xml");
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
    

	
	public String getCurrentDir() {
		return currentDir;
	}
    
    public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * create a new POM skeleton if no project.xml currently exists
	 * 
	* @throws Exception
	 */
	public void createPom() throws Exception {
		IFile pom = project.getFile("project.xml");
		if ( !new File(pom.getLocation().toOSString()).exists() ) {
			String skel = ProjectSkeleton.getSkeleton(project.getName());
			pom.create(new ByteArrayInputStream(skel.getBytes()), false, null);
		}
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
    
	/**
	 * @return the maven Project Object Model
	 */
	public File getPom() {
		return new File(new DefaultPathResolver().getAbsolutePath(project.getFile("project.xml").getLocation()));
	}
	
	public void initEnvironment() {
		Environment.setMavenHome(getMavenHome()); 
		Environment.setJavaHome(getJavaHome());
		Environment.setMavenRepository(getMavenRepository());
	}

}
