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

/**
 * interface to externalize the project files content/models to one place, and reuse
 * in all the queries (ILocationFinder, I PropertyResolver, etc)
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class DefaultQueryContext implements IQueryContext {
    private static final Log logger = LogFactory.getLog(DefaultQueryContext.class);
    
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
    
    public DefaultQueryContext() {
        String home = System.getProperty("user.home"); //NOI18N
        userDir = new File(home);
        userPropertyFile = new File(userDir, "build.properties"); //NOI18N
        userPropertyModel = new Properties();
    }
    
    public DefaultQueryContext(File project) {
        this();
        projectDir = project;
        projectPropertyFile = new File(projectDir, "project.properties"); //NOI18N
        buildPropertyFile = new File(projectDir, "build.properties"); //NOI18N
        projectPropertyModel = new Properties();
        buildPropertyModel = new Properties();
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
        return userDir;
    }
    
}
