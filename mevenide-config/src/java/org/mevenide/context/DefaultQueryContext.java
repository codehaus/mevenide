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

package org.mevenide.context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;

/**
 * interface to externalize the project files content/models to one place, and reuse
 * in all the queries (ILocationFinder, I PropertyResolver, etc)
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class DefaultQueryContext implements IQueryContext {
    private static final Log logger = LogFactory.getLog(DefaultQueryContext.class);
    private static final Project EMPTY_PROJECT = new Project();
    
    private File projectDir;
    private File userDir;
    private File userPropertyFile;
    private File buildPropertyFile;
    private File projectPropertyFile;
    
    private long userPropertyFileTimestamp = 0;
    private long buildPropertyFileTimestamp = 0;
    private long projectPropertyFileTimestamp = 0;
    
    private Properties userPropertyModel;
    private Properties buildPropertyModel;
    private Properties projectPropertyModel;
    
    // an empty default, fallback value.. in case it was not set.
    // mkleint does it make sense or is it better to throw NPE? calling it is a bug anyway without initialization.
    // it's kind of safer to have one before initializeProjectContext() is called.
    private IProjectContext projectContext = new EmptyProjectContext();
    
    // a few semi hacks introduced when creating a default non-project based instance.
    // reason to do: have the user.dir parsed just once..
    // I guess a special class should be introduced and then delegate from here,
    // but I didn't feel like doing it.. mkleint
    private static IQueryContext defaultInstance;
    
    /**
     * this constructor used only in the non-project based default instance.
     * that one handles the user.home related files and the project based one delegates to it.
     * -> user.home/build.properties file is read just once for all created contexts..
     */
    private DefaultQueryContext() {
        String home = System.getProperty("user.home"); //NOI18N
        userDir = new File(home);
        userPropertyFile = new File(userDir, "build.properties"); //NOI18N
        userPropertyModel = new Properties();
    }
    
    public DefaultQueryContext(File project) {
        projectDir = project;
        projectPropertyFile = new File(projectDir, "project.properties"); //NOI18N
        buildPropertyFile = new File(projectDir, "build.properties"); //NOI18N
        projectPropertyModel = new Properties();
        buildPropertyModel = new Properties();
    }
    /**
     * project context needs to be set after initialization from outside.
     * implementation comes from mevenide-core which depends on this one..
     */
    public void initializeProjectContext(IProjectContext projContext) {
        if (this == defaultInstance) {
            throw new IllegalStateException("Cannot set project context to the default querycontext instance.");
        }
        projectContext = projContext;
    }
    
    /**
     * the default instance that only refers to the user.dir properties file
     * non-project based querycontext. To be used only in cases where the project-based
     * context cannot be obtained for whatever reason.
     */
    public static synchronized IQueryContext getNonProjectContextInstance() {
        if (defaultInstance == null) {
             defaultInstance = new DefaultQueryContext();
        }
        return defaultInstance;
    }
    
    
    public String getBuildPropertyValue(String key) {
        buildPropertyFileTimestamp = checkReloadModel(buildPropertyFile, 
                                                      buildPropertyFileTimestamp,
                                                      buildPropertyModel);
        if (buildPropertyFileTimestamp == 0) {
            // file does not exist.
            return null;
        }
        return buildPropertyModel.getProperty(key);
    }
    
    public File getProjectDirectory() {
       return projectDir; 
    }
    
    public String getProjectPropertyValue(String key) {
        projectPropertyFileTimestamp = checkReloadModel(projectPropertyFile, 
                                                        projectPropertyFileTimestamp,
                                                        projectPropertyModel);
        if (projectPropertyFileTimestamp == 0) {
            // file does not exist.
            return null;
        }
        return projectPropertyModel.getProperty(key);
    }
    
    public String getPropertyValue(String key) {
        String toReturn = getProjectPropertyValue(key);
        if (toReturn == null) {
            toReturn = getBuildPropertyValue(key);
        }
        if (toReturn == null) {
            toReturn = getUserPropertyValue(key);
        }
        return toReturn;
    }
    
    public String getUserPropertyValue(String key) {
        //HACK - not nice here..
        if (this != defaultInstance) {
            return getNonProjectContextInstance().getUserPropertyValue(key);
        }
       userPropertyFileTimestamp = checkReloadModel(userPropertyFile, 
                                                    userPropertyFileTimestamp,
                                                    userPropertyModel);
        if (userPropertyFileTimestamp == 0) {
            // file does not exist.
            return null;
        }
        return userPropertyModel.getProperty(key);        
    }
    
    private long checkReloadModel(File propFile, long timestamp, Properties propModel) {
        if (propFile == null || !propFile.exists()) {
            return 0;
        }
        long lastModified = propFile.lastModified();
        if (lastModified > timestamp) {
            propModel.clear();
            try {
                propModel.load(new BufferedInputStream(new FileInputStream(propFile)));
            } catch (IOException exc) {
                logger.error("Error Reading a file that's supposed to exist. How come?", exc);
            }
                
        }
        return lastModified;
    }
    
    public File getUserDirectory() {
        //HACK - not nice here..
        if (this != defaultInstance) {
            return getNonProjectContextInstance().getUserDirectory();
        }
        return userDir;
    }
    public IProjectContext getPOMContext() {
        return projectContext;
    }
    
    
    private class EmptyProjectContext implements IProjectContext {
        private Project empty = new Project();
        public Project getFinalProject() {
            return empty;
        }
        
        public File[] getProjectFiles() {
            return new File[0];
        }
        
        public Project[] getProjectLayers() {
            return new Project[0];
        }
        
    }
}
