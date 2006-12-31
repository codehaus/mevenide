/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.jdom.Element;
import org.jdom.DefaultJDOMFactory;
import org.jdom.JDOMFactory;

/**
 * default implementation of IQueryContext.
 * to externalize the project files content/models to one place, and reuse
 * in all the queries (ILocationFinder, I PropertyResolver, etc)
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class DefaultQueryContext extends AbstractQueryContext {
    private static final Log logger = LogFactory.getLog(DefaultQueryContext.class);
    private static final Project EMPTY_PROJECT = new Project();

    private IQueryErrorCallback callback;
    private File projectDir;
    private File userDir;
    private File userPropertyFile;
//    private File buildPropertyFile;
//    private File projectPropertyFile;
//    private File parentBuildPropertyFile;
//    private File parentProjectPropertyFile;
    
    private long userPropertyFileTimestamp = 0;
//    private long buildPropertyFileTimestamp = 0;
//    private long projectPropertyFileTimestamp = 0;
//    private long parentBuildPropertyFileTimestamp = 0;
//    private long parentProjectPropertyFileTimestamp = 0;
    
    private Properties userPropertyModel;
//    private Properties buildPropertyModel;
//    private Properties projectPropertyModel;
//    private Properties parentBuildPropertyModel;
//    private Properties parentProjectPropertyModel;
    
    private HashMap propertyModels;
    private HashMap timestamps;
    private HashMap files;
    
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
        callback = new LoggerErrorHandlerCallback();
        String home = System.getProperty("user.home"); //NOI18N
        userDir = new File(home);
        userPropertyFile = new File(userDir, "build.properties"); //NOI18N
        userPropertyModel = new Properties();
        initMaps();
    }
   
    /**
     * create a new project context.
     * @param projectDirectory the directory where the project.xml file is located.
     */
    public DefaultQueryContext(File projectDirectory) {
        this(projectDirectory, new LoggerErrorHandlerCallback());
    }   
    
    /**
     * create a new project context.
     * @param projectDirectory the directory where the project.xml file is located.
     * @param errorCallback callback which gets notified when reading/parsing errors occur 
     */
    public DefaultQueryContext(File projectDirectory, IQueryErrorCallback errorCallback) {
        callback = errorCallback;
        projectDir = projectDirectory;
//        projectPropertyFile = new File(projectDir, "project.properties"); //NOI18N
//        buildPropertyFile = new File(projectDir, "build.properties"); //NOI18N
//        projectPropertyModel = new Properties();
//        buildPropertyModel = new Properties();
//        parentProjectPropertyModel = new Properties();
//        parentBuildPropertyModel = new Properties();
        initMaps();
        projectContext = new DefaultProjectContext(this, this.getResolver(), callback);
    }
    
    /**
     * test constructor only
     */
    DefaultQueryContext(File projectDirectory, IProjectContext proj) {
        projectDir = projectDirectory;
//        projectPropertyFile = new File(projectDir, "project.properties"); //NOI18N
//        buildPropertyFile = new File(projectDir, "build.properties"); //NOI18N
//        projectPropertyModel = new Properties();
//        buildPropertyModel = new Properties();
//        parentProjectPropertyModel = new Properties();
//        parentBuildPropertyModel = new Properties();
        projectContext = proj;
        initMaps();
    }
   
    private void initMaps() {
        propertyModels = new HashMap();
        timestamps = new HashMap();
        files = new HashMap();
        if (projectDir != null) {
            initLevel(1, projectDir);
        }
    }
    
    void initLevel(int level, File projDir) {
        File f = new File(projDir, "project.properties"); //NOI18N
        Integer location = new Integer(level * 10);
        files.put(location, f);
        timestamps.put(location, new Long(0));
        propertyModels.remove(location);
        f = new File(projDir, "build.properties"); //NOI18N
        location = new Integer(level * 10 + 1);
        files.put(location, f);
        timestamps.put(location, new Long(0));
        propertyModels.remove(location);
    }
    
    void reloadingParents() {
        // kind of gross, would be better to iterate and remove all 
        // but the project's own entries. (locations 10+11)
        files.clear();
        timestamps.clear();
        propertyModels.clear();
        initLevel(1, projectDir);
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
        return getPropertyValueAt(key, 11);
    }
    
    public File getProjectDirectory() {
       return projectDir; 
    }
    
    public String getProjectPropertyValue(String key) {
        return getPropertyValueAt(key, 10);
    }
    
   public String getParentBuildPropertyValue(String key) {
        return getPropertyValueAt(key, 21); 
   }    
   
    public String getParentProjectPropertyValue(String key) {
        return getPropertyValueAt(key, 20);
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
    
    private Properties checkReloadModel(int location) {
        if (location > 19) {
            // now if if are querying more than the project's own pom file
            // we need to recheck the timestamps on the 
            getPOMContext().getProjectFiles();
        }
        Integer loc = new Integer(location);
        File propFile = (File)files.get(loc);
        if (propFile == null || !propFile.exists()) {
            return null;
        }
        long lastModified = propFile.lastModified();
        Long timeStamp = (Long)timestamps.get(loc);
        Properties prop = (Properties)propertyModels.get(loc);
        if (prop == null) {
            prop = new Properties();
            propertyModels.put(loc, prop);
        }
        if (lastModified > timeStamp.longValue()) {
            prop.clear();
            InputStream str = null;
            try {
                str = new BufferedInputStream(new FileInputStream(propFile));
                prop.load(str);
            } catch (IOException exc) {
                callback.handleError(IQueryErrorCallback.ERROR_UNREADABLE_PROP_FILE, exc);
            } finally {
                timestamps.put(loc, new Long(lastModified));
                if (str != null) {
                    try {
                        str.close();
                    } catch (IOException exc) {
                        logger.error("Cannot close " + propFile, exc);
                    }
                }
            }
        }
        return prop;
    }
    
    private long checkReloadModel(File propFile, long timestamp, Properties propModel) {
        if (propFile == null || !propFile.exists()) {
            return 0;
        }
        long lastModified = propFile.lastModified();
        if (lastModified > timestamp) {
            propModel.clear();
            InputStream str = null;
            try {
                str = new BufferedInputStream(new FileInputStream(propFile));
                propModel.load(str);
            } catch (IOException exc) {
                callback.handleError(IQueryErrorCallback.ERROR_UNREADABLE_PROP_FILE, exc);
            } finally {
                if (str != null) {
                    try {
                        str.close();
                    } catch (IOException exc) {
                        logger.error("Cannot close " + propFile, exc);
                    }
                }
            }
                
        }
        return lastModified;
    }
    
//    private long checkParentReloadModel(File propFile, long timestamp, Properties propModel, String name) {
//        IProjectContext pom = getPOMContext();
//        if (pom == null) {
//            return 0;
//        }
//        File[] files = pom.getProjectFiles();
//        if (files == null || files.length < 2) {
//            return 0;
//        }
//        File newpropfile = new File(files[1].getParentFile(), name);
//        if (!newpropfile.equals(propFile)) {
//            propFile = newpropfile;
//            timestamp = 0;
//        }
//        if (propFile == null || !propFile.exists()) {
//            return 0;
//        }
//        long lastModified = propFile.lastModified();
//        if (lastModified > timestamp) {
//            propModel.clear();
//            try {
//                propModel.load(new BufferedInputStream(new FileInputStream(propFile)));
//            } catch (IOException exc) {
//                callback.handleError(IQueryErrorCallback.ERROR_UNREADABLE_PROP_FILE, exc);
//            }
//                
//        }
//        return lastModified;
//    }
    
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

    public Set getBuildPropertyKeys() {
        return getPropertyKeysAt(11);
    }

    public Set getProjectPropertyKeys() {
        return getPropertyKeysAt(10);
    }

    public Set getUserPropertyKeys() {
        //HACK - not nice here..
        if (this != defaultInstance) {
            return getNonProjectContextInstance().getUserPropertyKeys();
        }
       userPropertyFileTimestamp = checkReloadModel(userPropertyFile, 
                                                    userPropertyFileTimestamp,
                                                    userPropertyModel);
        if (userPropertyFileTimestamp == 0) {
            // file does not exist.
            return Collections.EMPTY_SET;
        }
        return new HashSet(userPropertyModel.keySet());        
    }
    
   public Set getParentBuildPropertyKeys() {
        return getPropertyKeysAt(21);
   }

    public Set getParentProjectPropertyKeys() {
        return getPropertyKeysAt(20);
    }    
    

    public String getPropertyValueAt(String key, int location) {
        if (location == 12) {
            return getUserPropertyValue(key);
        }
        Properties props = checkReloadModel(location);
        if (props == null) {
            // file does not exist.
            return null;
        }
        return props.getProperty(key);
    }
    
    public Set getPropertyKeysAt(int location) {
        if (location == 12) {
            return getUserPropertyKeys();
        }
        Properties props = checkReloadModel(location);
        if (props == null) {
            // file does not exist.
            return Collections.EMPTY_SET;
        }
        return new HashSet(props.keySet());
        
    }
    
   public final String getPropertyValue(String key) {
        String toReturn = getUserPropertyValue(key);
        if (toReturn == null) {
            toReturn = getBuildPropertyValue(key);
        }
        if (toReturn == null) {
            toReturn = getProjectPropertyValue(key);
        }
        if (toReturn == null) {
            int depth = getPOMContext().getProjectDepth();
            if (depth > 1) {
                // start at 1, we already checked that before, no need to do again..
                // for performance reasons we did it without checking the pomcontext
                int current = 1;
                while (current <= depth && toReturn == null) {
                    current = current + 1;
                    toReturn = getPropertyValueAt(key, current * 10 + 1);
                    if (toReturn == null) {
                        toReturn = getPropertyValueAt(key, current * 10);
                    }
                }
            }
        }
        return toReturn;
    }    
    
    
    // is this necesaary, maybe just return null from getRootProjectElement()
    private static JDOMFactory factory = new DefaultJDOMFactory();   

    private class EmptyProjectContext implements IProjectContext {
        private Project empty = new Project();
        private Element rootEl = factory.element("project");
        public Project getFinalProject() {
            return empty;
        }
        
        public File[] getProjectFiles() {
            return new File[0];
        }
        
        public Project[] getProjectLayers() {
            return new Project[0];
        }
        
        public int getProjectDepth() {
            return 0;
        }
        
        public org.jdom.Element[] getRootElementLayers() {
            return new Element[0];
        }
        
        public org.jdom.Element getRootProjectElement() {
            return rootEl;
        }
        
    }
}
