/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

package org.mevenide.environment;

import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyResolver;

/**  
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 */
public class LocationFinderAggregator extends AbstractLocationFinder {
    
    private SysEnvLocationFinder sysEnvLocationFinder;
    private ILocationFinder customLocationFinder;
    private IPropertyResolver resolver;

    /**
     * Use the default context (non-project based) WARNING use just when you
     * know what you are doing.
     */
    LocationFinderAggregator() {
        this(null, null);
    }

    /**
     * Use the default context (non-project based) WARNING use just when you
     * know what you are doing.
     */
    LocationFinderAggregator(ILocationFinder custom) {
        this(null, custom);
    }

    /**
     * default constructor for the aggregator
     * 
     * @param queryContext the project's query context.
     */
    public LocationFinderAggregator(IQueryContext queryContext) {
        this(queryContext, null);
    }

    /**
     * default constructor for the aggregator
     * 
     * @param queryContext the project's query context.
     */
    public LocationFinderAggregator(IQueryContext queryContext, ILocationFinder custom) {
        sysEnvLocationFinder = SysEnvLocationFinder.getInstance();
        IQueryContext context = (queryContext == null)? DefaultQueryContext.getNonProjectContextInstance(): queryContext;
        resolver = context.getResolver();
        customLocationFinder = (custom == null)? new MissingLocationFinder(): custom;
    }

    public String getJavaHome() {
        String javaHome = System.getProperty(JAVA_HOME);
        if ( customLocationFinder.getJavaHome() != null ) {
            javaHome = customLocationFinder.getJavaHome();
        }
        String resValue = resolver.getResolvedValue(JAVA_HOME);
        if (resValue != null) {
            javaHome = resValue;
        }
        if ( sysEnvLocationFinder !=  null
          && sysEnvLocationFinder.getJavaHome() != null ) {
            javaHome = sysEnvLocationFinder.getJavaHome();
        }
        return javaHome;
    }
    
    public String getMavenHome() {
        // does it make sense to consult the resolver.. MAVEN_HOME *has* to be set..
        String mavenHome = null;
        if ( customLocationFinder.getMavenHome() != null ) {
            mavenHome = customLocationFinder.getMavenHome();
        }
        String resValue = resolver.getResolvedValue(MAVEN_HOME);
        if (resValue != null) {
            mavenHome = resValue;
        }
        if (   sysEnvLocationFinder !=  null
            && sysEnvLocationFinder.getMavenHome() != null ) {
            mavenHome = sysEnvLocationFinder.getMavenHome();
        }
        return mavenHome;
    }
    
    public String getMavenLocalHome() {
	    String mavenLocalHome = super.getMavenLocalHome();
        if ( customLocationFinder.getMavenLocalHome() != null ) {
            mavenLocalHome = customLocationFinder.getMavenLocalHome();
        }    
        String resValue = resolver.getResolvedValue(MAVEN_HOME_LOCAL);
        if (resValue != null) {
            mavenLocalHome = resValue;
        }
        return mavenLocalHome;
    }
    
    public String getMavenLocalRepository() {
        String mavenLocalRepository = super.getMavenLocalRepository();
        if ( customLocationFinder.getMavenLocalRepository() != null ) {
            mavenLocalRepository = customLocationFinder.getMavenLocalRepository();
        }
        String resValue = resolver.getResolvedValue(MAVEN_REPO_LOCAL);
        if (resValue != null) {
            mavenLocalRepository = resValue;
        }
        return mavenLocalRepository;
    }
    
    public String getMavenPluginsDir() {
        String mavenPluginsDir = super.getMavenPluginsDir();
        if ( customLocationFinder.getMavenPluginsDir() != null ) {
            mavenPluginsDir = customLocationFinder.getMavenPluginsDir();
        }
        String resValue = resolver.getResolvedValue(MAVEN_PLUGIN_UNPACKED_DIR);
        if (resValue != null) {
            mavenPluginsDir = resValue;
        }
        return mavenPluginsDir;
    }
    
    public String getUserPluginsDir() {
        String pluginsDir = super.getUserPluginsDir();
        if ( customLocationFinder.getUserPluginsDir() != null ) {
            pluginsDir = customLocationFinder.getUserPluginsDir();
        }
        String resValue = resolver.getResolvedValue(MAVEN_PLUGIN_USER_DIR);
        if (resValue != null) {
            pluginsDir = resValue;
        }
        return pluginsDir;
    }  
    
    public String getPluginJarsDir() {
        String pluginsDir = super.getPluginJarsDir();
        if ( customLocationFinder.getPluginJarsDir() != null ) {
            pluginsDir = customLocationFinder.getPluginJarsDir();
        }
        String resValue = resolver.getResolvedValue(MAVEN_PLUGIN_DIR);
        if (resValue != null) {
            pluginsDir = resValue;
        }
        return pluginsDir;
    }        
    
    public String getUserHome() {
        String userHome = System.getProperty(USER_HOME);
        if ( sysEnvLocationFinder !=  null
          && sysEnvLocationFinder.getUserHome() != null ) {
            userHome = sysEnvLocationFinder.getUserHome();
        }
        return userHome;
    }    
    
    /**
     * @param customLocationFinder
     * @deprecated Pass the custom location finder in the constructor.
     */
    public void setCustomLocationFinder(ILocationFinder locationFinder) {
        this.customLocationFinder = (locationFinder == null)? new MissingLocationFinder(): locationFinder;
    }

}