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
package org.mevenide.environment;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyResolverFactory;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: LocationFinderAggregator.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class LocationFinderAggregator implements ILocationFinder {
    
    private static Log log = LogFactory.getLog(LocationFinderAggregator.class);
    
    private SysEnvLocationFinder sysEnvLocationFinder;
//    private CustomLocationFinder customLocationFinder;
    
    private IQueryContext context;
    private IPropertyResolver resolver;
    /**
     * Use the default context.
     */
    public LocationFinderAggregator() {
        this(DefaultQueryContext.getNonProjectContextInstance());
    }
    
    public LocationFinderAggregator(IQueryContext queryContext) {
        sysEnvLocationFinder = SysEnvLocationFinder.getInstance();
        context = queryContext;
        resolver = PropertyResolverFactory.getFactory().createContextBasedResolver(context);
        //TODO - have a singleton instance of user home's properties..
//        buildPropertiesLocationFinder = new BuildPropertiesLocationFinder(context);
//        userRefinedPropertiesLocationFinder = new UserRefinedPropertiesLocationFinder(context);
//        projectPropertiesLocationFinder = new ProjectPropertiesLocationFinder(context);
    }
    
//    /**
//     * @deprecated create the LocatioFinderAggregator with the IQueryContext constructor instead.
//     * This workflow parses the properties files too often.
//     */
//    public void setEffectiveWorkingDirectory(String effectiveWorkingDirectory) {
//        if (context != null) {
//            throw new IllegalStateException("Possibly out-of-sych situation. Using IQueryContext and" + 
//                                            "calling setEffectiveWorkingDirectory");
//        }
//        try {
//	        userRefinedPropertiesLocationFinder = new UserRefinedPropertiesLocationFinder(effectiveWorkingDirectory);
//        }
//		catch ( Exception e ) { 
//			log.debug("UserRefinedPropertiesLocationFinder not created", e);
//		}
//        try {
//            projectPropertiesLocationFinder = new ProjectPropertiesLocationFinder(effectiveWorkingDirectory);
//        }
//		catch ( Exception e ) { 
//			log.debug("ProjectPropertiesLocationFinder not created", e);
//		}
//    }

    public String getConfigurationFileLocation() {
        if ( getMavenHome() != null ) {
            File conf = new File(new File(getMavenHome(), "bin"), "forehead.conf");
            if ( conf.exists() ) {
                return conf.getAbsolutePath();
            }
        }
        return null;
    }

    public String getJavaHome() {
        String javaHome = System.getProperty("java.home");
        String resValue = resolver.getResolvedValue("java.home");
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
        String mavenHome = resolver.getResolvedValue("maven.home");
        if (   sysEnvLocationFinder !=  null
            && sysEnvLocationFinder.getMavenHome() != null ) {
            mavenHome = sysEnvLocationFinder.getMavenHome();
        }
        return mavenHome;
    }
    
    public String getMavenLocalHome() {
        String userHome = System.getProperty("user.home");
	String mavenLocalHome = new File(userHome, ".maven").getAbsolutePath();
        String resValue = resolver.getResolvedValue("maven.home.local");
        if (resValue != null) {
            mavenLocalHome = resValue;
        }
        return mavenLocalHome;
    }
    
    public String getMavenLocalRepository() {
        String mavenLocalRepository =  new File(getMavenLocalHome(), "repository").getAbsolutePath();
        String resValue = resolver.getResolvedValue("maven.repo.local");
        if (resValue != null) {
            mavenLocalRepository = resValue;
        }
        return mavenLocalRepository;
    }
    
    public String getMavenPluginsDir() {
        String mavenPluginsDir = new File(getMavenLocalHome(), "cache").getAbsolutePath();
        String resValue = resolver.getResolvedValue("maven.plugin.unpacked.dir");
        if (resValue != null) {
            mavenPluginsDir = resValue;
        }
        return mavenPluginsDir;
    }
    
    public String getUserHome() {
        String userHome = System.getProperty("user.home");
        if ( sysEnvLocationFinder !=  null
          && sysEnvLocationFinder.getUserHome() != null ) {
            userHome = sysEnvLocationFinder.getUserHome();
        }
        return userHome;
    }    
  
//    public void setCustomLocationFinder(CustomLocationFinder customLocationFinder) {
//        this.customLocationFinder = customLocationFinder;
//    }
}
