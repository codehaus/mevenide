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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @refactor ECLIPSEAPI get rid of all get&lt;file&gt; method so that they use <code>getStateLocation()</code> when needed
 * @refactor EXTRACTME lots of non related utility methods
 *  
 */
public class MavenPlugin extends AbstractUIPlugin {
	
	private static MavenPlugin plugin;
	
	public static final String NATURE_ID = "mavenplugin.mavennature";
	
	private ResourceBundle resourceBundle;
	
	private String mavenHome;
    private String javaHome;
    private String mavenRepository;
    
    private String currentDir;
    private IProject project;
    
    public MavenPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		try {
            plugin = this;
            PreferenceStore preferenceStore = new PreferenceStore(getPreferencesFilename());
            preferenceStore.load();
            
            mavenHome = preferenceStore.getString("maven.home");
            javaHome = preferenceStore.getString("java.home");
            resourceBundle = ResourceBundle.getBundle("MavenPluginResources");
            
//			IResourceChangeListener listener = AutoSynchronizer.getSynchronizer();
//			ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
			
		} 
		catch (Exception x) {
			x.printStackTrace();
			resourceBundle = null;
		}
	}

 

    /** 
     * @return MavenPlugin
     */
	public static MavenPlugin getPlugin() {
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
		ResourceBundle bundle= MavenPlugin.getPlugin().getResourceBundle();
		try {
			return bundle.getString(key);
		} 
		catch (MissingResourceException e) {
			e.printStackTrace();
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
		
	}
	
    public static ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = MavenPlugin.getResourceString("IconsPath");
        try {
            URL installURL = MavenPlugin.getPlugin().getDescriptor().getInstallURL();
            URL url = new URL(installURL, iconPath + "/" + relativePath);
            return ImageDescriptor.createFromURL(url);
        } 
        catch (MalformedURLException e) {
            // should not happen
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
    }

    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
    }
    
	public void setMavenRepository(String mavenRepository) {
		this.mavenRepository = mavenRepository;
	}
	
    public String getPreferencesFilename() {
		return getFile("prefs.ini");   
    }
	
	public String getGoalsPreferencesFilename() {
		return getFile("goals_prefs.ini");
	}
	
	private String getFile(String fname) {
		try {
			URL installBase = MavenPlugin.getPlugin().getDescriptor().getInstallURL();
			File f = new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath(), fname);
			return f.getAbsolutePath();
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
    public String getXmlGoalsFile() {
        return getFile("maven-goals.xml");
    }
    
    public String getEffectiveDirectory() {
        try {
        	URL installBase = MavenPlugin.getPlugin().getDescriptor().getInstallURL();
        	return new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath()).toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }   
        
    }
    
    public String getForeheadConf() {
        try {
            URL installBase = MavenPlugin.getPlugin().getDescriptor().getInstallURL();
            File f = new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath(), "conf.file");
            return f.getAbsolutePath();
        }
        catch (IOException e) {
            e.printStackTrace();
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

    
}
