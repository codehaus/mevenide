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

package org.mevenide.properties.resolver;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class PropertyFilesAggregator implements IPropertyResolver, IPropertyLocator {
    private static final Log logger = LogFactory.getLog(PropertyFilesAggregator.class);
    
    private File projectDir;
    private File userDir;
    private IPropertyFinder project;
    private IPropertyFinder projectBuild;
    private IPropertyFinder userBuild;
    private IPropertyFinder defaults;
    private IPropertyFinder projectWalker;
    
    private IQueryContext context;

    /** 
     * Creates a new instance of PropFilesAggregator 
     * @param project parent directory of project.xml
     * @param user the user home directory
     * @param defs property finder which override properies definition that may be found in <code>user</code> and <code>project</code> properties files 
     * @deprecated use IQueryContext based constructor
     */
    PropertyFilesAggregator(File project, File user, DefaultsResolver defs) {
        projectDir = project;
        userDir = user;
        defaults = defs;
        initialize();
        // cannot use the ILocationFinder here, since Loc finders use resolvers -> cyclic dependency
        String val = getResolvedValue("maven.plugin.unpacked.dir"); //NOI18N
        System.out.println("plugin=" + val);
        if (val != null) {
            defs.initPluginPropsFinder(PropertyResolverFactory.getFactory().getPluginDefaultsPropertyFinder(new File(val)));
        }
    }
    
    /**
     * IQueryContext based constructor. not public, use PropertyResolverfactory
     */
    PropertyFilesAggregator(IQueryContext querycontext, DefaultsResolver defs) {
        context = querycontext;
        defaults = defs;
        initializeContext();
        // cannot use the ILocationFinder here, since Loc finders use resolvers -> cyclic dependency
        String val = getResolvedValue("maven.plugin.unpacked.dir"); //NOI18N
        if (val != null) {
            defs.initPluginPropsFinder(PropertyResolverFactory.getFactory().getPluginDefaultsPropertyFinder(new File(val)));
        }
    }
    
    private void initialize() { 
        if (context != null) {
            throw new IllegalStateException("wrong initializer");
        }
        
    	File fo = new File(projectDir, "project.properties");
        if ( fo.exists() ) {
        	project = new SinglePropertyFileFinder(fo);
        }
        fo = new File(projectDir, "build.properties");
        if ( fo.exists() ) {
        	projectBuild = new SinglePropertyFileFinder(fo);
        }
        //TODO.. have some caching for user prop file or reuse one instance in all the 
        // agrregators.
        fo = new File(userDir, "build.properties");
        if ( fo.exists() ) {
        	userBuild = new SinglePropertyFileFinder(fo);
        }
    }
    
    private void initializeContext() {
        if (context == null) {
            throw new IllegalStateException("wrong initializer");
        }
        userBuild = QueryBasedFinderFactory.createUserPropertyFinder(context);
        project = QueryBasedFinderFactory.createProjectPropertyFinder(context);
        projectBuild = QueryBasedFinderFactory.createBuildPropertyFinder(context);
        projectWalker = new ProjectWalker2(context);
    }
    
    public String getResolvedValue(String key) {
        return getValue(key, true);
    }
   
    
    public String getValue(String key) {
        return getValue(key, false);
    }
    
    protected String getValue(String key, boolean resolve) {
        String toReturn = null;
        if (key.startsWith("pom.") && projectWalker != null) {
            toReturn = projectWalker.getValue(key);
        } else {
            toReturn = checkSysEnv(key);
            if (toReturn == null && context != null) {
                toReturn = context.getPropertyValue(key);
            } else {
                if (toReturn == null && userBuild != null) {
                    toReturn = userBuild.getValue(key);
                }
                if (toReturn == null && projectBuild != null ) {
                    toReturn = projectBuild.getValue(key);
                }
                if (toReturn == null && project != null ) {
                    toReturn = project.getValue(key);
                }
            }
            if (toReturn == null && defaults != null ) {
                toReturn = defaults.getValue(key);
            }
        }
        if (resolve && toReturn != null) {
            toReturn = resolve(new StringBuffer(toReturn)).toString();
        }
        return toReturn;
    }
    
    private String checkSysEnv(String key) {
        if ("maven.home.local".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenLocalHome();
        }
        if ("maven.home".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenHome();
        }
        if ("maven.repo.local".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenLocalRepository();
        }
        return null;
    }

    /**
     * IPropertyLocator method, identifying where the property comes from.
     */
    public int getPropertyLocation(String key) {
        int toReturn = IPropertyLocator.LOCATION_NOT_DEFINED;
        if (checkSysEnv(key) != null) {
            return IPropertyLocator.LOCATION_SYSENV;
        }
        if (userBuild != null && userBuild.getValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_USER_BUILD;
        }
        else if (projectBuild != null && projectBuild.getValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PROJECT_BUILD;
        }
        else if (project != null && project.getValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PROJECT;
        } 
        else if (context != null && context.getParentBuildPropertyValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD;
        }
        else if (context != null && context.getParentProjectPropertyValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PARENT_PROJECT;
        }
        else if (defaults != null && defaults.getValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_DEFAULTS;
        }
        return toReturn;
    }
    
    
    protected final StringBuffer resolve(StringBuffer value) {
        StringBuffer toReturn = value;
        int index = value.indexOf("${");
        if (index > -1) {
            int end = value.indexOf("}", index);
            if (end > index + 2) {
                String key = value.substring(index + 2, end);
                String keyvalue = getValue(key, false);
                if (keyvalue != null) {
                    toReturn.replace(index, end + 1, keyvalue);
                    return resolve(toReturn);
                } else {
                    logger.warn("cannot resolve key? '" + key + "'");
                }
            } else {
                logger.warn("badly formed value? '" + value + "'");
            }
        } 
        return toReturn;
    }
    
    /**
     *@deprecated makes no sense for IQueryContext based instances.
     */
    public void reload() {
        // mkleint - makes no sense for IQueryContext based instances.
        //TODO have more targetting reload strategy.
    	reload(userBuild);
    	reload(project);
    	reload(projectBuild);
        reload(projectWalker);
    }
    
    private void reload(IPropertyFinder finder) {
        if ( finder != null ) {
    		finder.reload();
    	}
    }

    public String resolveString(String original) {
        if (original ==  null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(original);
        return resolve(buf).toString();
    }        
    
    public boolean isDefinedInLocation(String key, int location) {
        if (location == IPropertyLocator.LOCATION_USER_BUILD) {
            return (userBuild != null && userBuild.getValue(key) != null);
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT_BUILD) {
            return (projectBuild != null && projectBuild.getValue(key) != null);
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT) {
           return (project != null && project.getValue(key) != null);
        }
        else if (location == IPropertyLocator.LOCATION_DEFAULTS) {
            return (defaults != null && defaults.getValue(key) != null);
        } 
        else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT) {
            return (context != null && context.getParentProjectPropertyValue(key) != null);
        }
        else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD) {
            return (context != null && context.getParentBuildPropertyValue(key) != null);
        }
        return false;        
    } 
    
    public String getValueAtLocation(String key, int location) {
        if (location == IPropertyLocator.LOCATION_USER_BUILD && userBuild != null) {
            return userBuild.getValue(key);
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT_BUILD && projectBuild != null) {
            return projectBuild.getValue(key);
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT && project != null) {
           return project.getValue(key);
        }
        else if (location == IPropertyLocator.LOCATION_DEFAULTS && defaults != null) {
            return defaults.getValue(key);
        } 
        else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT && context != null) {
            return context.getParentProjectPropertyValue(key);
        }
        else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD && context != null) {
            return context.getParentBuildPropertyValue(key);
        }
        return null;
    }
    
    /**
     * returns all the keys at the given location.
     */
    public Set getKeysAtLocation(int location) {
        if (context == null) {
            // Mkleint - no implementation for non-query based instance
            return Collections.EMPTY_SET;
        }
        if (location == IPropertyLocator.LOCATION_USER_BUILD) {
            return context.getUserPropertyKeys();
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT_BUILD) {
            return context.getBuildPropertyKeys();
        }
        else if (location == IPropertyLocator.LOCATION_PROJECT) {
           return context.getProjectPropertyKeys();
        } else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT) {
            return context.getParentProjectPropertyKeys();
        } else if (location == IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD) {
            return context.getParentBuildPropertyKeys();
        }
        else if (location == IPropertyLocator.LOCATION_DEFAULTS) {
            if (defaults != null && defaults instanceof DefaultsResolver) {
                return ((DefaultsResolver)defaults).getDefaultKeys();
            }
        }
        return Collections.EMPTY_SET;        
    }
    
}
