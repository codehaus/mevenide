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
package org.mevenide.project.io;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.mevenide.util.StringUtils;

/**
 * A QueryContext based replacement for JarOverrideReader.
 * The original is somewhat flawed and has low performance (too many IO operations)
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public final class JarOverrideReader2 {
    private static Log log = LogFactory.getLog(JarOverrideReader2.class);
    
    private static JarOverrideReader2 instance;
    JarOverrideReader2() {
    }
    
    public static JarOverrideReader2 getInstance() {
        if (instance == null) {
            instance = new JarOverrideReader2();
        }
        return instance;
    }

    public void processOverride(Project project, IPropertyResolver resolver, ILocationFinder finder) {
        List deps = project.getDependencies();
        if (deps != null) {
            Iterator it = deps.iterator();
            while (it.hasNext()) {
                Dependency dep = (Dependency)it.next();
                String override = processOverride(dep, resolver, finder);
                if (override != null) {
                    dep.setJar(override);
                }
            }
        }
    }
    
    public void processOverride(Project project, IQueryContext context) {
        IPropertyResolver resolver = PropertyResolverFactory.getFactory().createContextBasedResolver(context);
        ILocationFinder finder = new LocationFinderAggregator(context);
        processOverride(project, resolver, finder);
    }
    
    /**
     * Checks if with the given context  the dependency is overriden.
     * if so, returns the override's absolute path, otherwise null.
     * @returns absolute path to the override jar, or null if not overriding
     */
    public String processOverride(Dependency dependency, IQueryContext context) {
        IPropertyResolver resolver = PropertyResolverFactory.getFactory().createContextBasedResolver(context);
        ILocationFinder finder = new LocationFinderAggregator(context);
        return processOverride(dependency, resolver, finder);
    }
    /**
     * Checks if with the given context  the dependency is overriden.
     * if so, returns the override's absolute path, otherwise null.
     * @returns absolute path to the override jar, or null if not overriding
     */
    public String processOverride(Dependency dependency, IPropertyResolver resolver, ILocationFinder finder) {
        boolean isJarOverrideOn = isJarOverrideOn(resolver);
        log.debug("jar override " + (isJarOverrideOn ? " on " : " off"));
        if ( isJarOverrideOn ) {
                String dependencyOverrideValue = getOverrideValue(dependency, resolver, finder);
                log.debug("overriding jar for dep : " + dependency.getId() + " : " + dependencyOverrideValue);
                return dependencyOverrideValue;
        }
        return null;
    }
    
    private String getOverrideValue(Dependency dependency, IPropertyResolver resolver, ILocationFinder finder) {
        String key = "maven.jar." + dependency.getArtifactId();
        String dependencyOverrideValue =  resolver.getResolvedValue(key);
        if ( !StringUtils.isNull(dependencyOverrideValue) ) {
            String versionOverrideValue = checkVersionOverrideValue(dependency, dependencyOverrideValue.trim(), resolver, finder);
            if ( versionOverrideValue != null )  {
                dependencyOverrideValue = versionOverrideValue;
            }
        }
        return dependencyOverrideValue;
    }
    
    private String checkVersionOverrideValue(Dependency dependency, String dependencyOverrideValue,
                                             IPropertyResolver resolver, ILocationFinder finder) {
        if ( Character.isDigit(dependencyOverrideValue.charAt(0)) ) {
            File artifactGroupPath = new File(finder.getMavenLocalRepository(), dependency.getGroupId());
            //only jars are added to .classpath
            File artifactTypePath = new File(artifactGroupPath, "jars");
            File artifactPath = new File(artifactTypePath, dependency.getArtifactId() + "-" + dependencyOverrideValue + ".jar");
            return artifactPath.getAbsolutePath();
        }
        return null;
    }
    
    private boolean isJarOverrideOn(IPropertyResolver resolver) {
        String prop = resolver.getResolvedValue("maven.jar.override"); //NOI18N
        if (prop != null) {
            prop = prop.trim();
            if ( "on".equalsIgnoreCase(prop)
               || "1".equals(prop)
               || "true".equalsIgnoreCase(prop)) 
            {
                return true;
            }
        }
        return false;
    }
}
