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

package org.mevenide.environment;

import junit.framework.*;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.TestQueryContext;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.sysenv.SysEnvProvider;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyResolverFactory;

/**
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class LocationFinderAggregatorTest extends TestCase {
    private TestQueryContext context;
    private File userHomeDir;
    private File projectDir;
    private TestProvider sysenvprovider;
    
    protected void setUp() throws java.lang.Exception {
        sysenvprovider = new TestProvider();
        context = new TestQueryContext();
        String userHome = System.getProperty("user.home"); //NOI18N
        userHomeDir  = new File(userHome, ".mevenide_test");
        if (!userHomeDir.exists()) {
            userHomeDir.mkdir();
        }
        projectDir = new File (userHomeDir, "test_project");
        if (!projectDir.exists()) {
            projectDir.mkdir();
        }
        context.setProjectDirectory(projectDir);
        
    }
    
    protected void tearDown() throws Exception {
        delete(userHomeDir);
    }
    
    protected void delete(File file) {
        if ( file.isFile() ) {
            file.delete();
        }
        else {
            File[] files = file.listFiles();
            if ( files != null ) {
                for (int i = 0; i < files.length; i++) {
                    delete(files[i]);
                }
            }
            file.delete();
        }
        
    }

    /**
     * Test of getJavaHome method, of class org.mevenide.environment.LocationFinderAggregator.
     */
    public void testGetJavaHome() {
        String value = "javahome test";
        sysenvprovider.setJavaHome(value);
        SysEnvLocationFinder.setDefaultSysEnvProvider(sysenvprovider);
        LocationFinderAggregator aggr = new LocationFinderAggregator(context);
        assertEquals(value, aggr.getJavaHome());
    }

    /**
     * Test of getMavenHome method, of class org.mevenide.environment.LocationFinderAggregator.
     */
    public void testGetMavenHome() {
        String value = "mavenhomevalue";
        SysEnvLocationFinder.setDefaultSysEnvProvider(sysenvprovider);
        context.addProjectPropertyValue("maven.home", value);
        LocationFinderAggregator aggr = new LocationFinderAggregator(context);
        assertEquals(value, aggr.getMavenHome());
        value = "sysenv value overrides the value in project";
        sysenvprovider.setMavenHome(value);
        assertEquals(value, aggr.getMavenHome());
    }

    /**
     * Test of getMavenLocalHome method, of class org.mevenide.environment.LocationFinderAggregator.
     */
    public void testGetMavenLocalHome() {
        String value = "mavenhomelocalvalue";
        SysEnvLocationFinder.setDefaultSysEnvProvider(sysenvprovider);
        context.addProjectPropertyValue("maven.home.local", value);
        LocationFinderAggregator aggr = new LocationFinderAggregator(context);
        assertEquals(value, aggr.getMavenLocalHome());
        value = "sysenv value overrides the value in project";
        sysenvprovider.setMavenLocalHome(value);
        assertEquals(value, aggr.getMavenLocalHome());
    }

    /**
     * Test of getMavenLocalRepository method, of class org.mevenide.environment.LocationFinderAggregator.
     */
    public void testGetMavenLocalRepository() {
        String value = "mavenrepolocalvalue";
        SysEnvLocationFinder.setDefaultSysEnvProvider(sysenvprovider);
        context.addProjectPropertyValue("maven.repo.local", value);
        LocationFinderAggregator aggr = new LocationFinderAggregator(context);
        assertEquals(value, aggr.getMavenLocalRepository());
        context.addBuildPropertyValue("maven.home.local", "original home");
        context.addProjectPropertyValue("maven.repo.local", "${maven.home.local}/repo");
        assertEquals("original home/repo", aggr.getMavenLocalRepository());
        value = "sysenv value";
        sysenvprovider.setMavenLocalHome(value);
        assertEquals(value + "/repo", aggr.getMavenLocalRepository());
    }

    /**
     * Test of getMavenPluginsDir method, of class org.mevenide.environment.LocationFinderAggregator.
     */
    public void testGetMavenPluginsDir() {
        String value = "mavenpluginsdirvalue";
        SysEnvLocationFinder.setDefaultSysEnvProvider(sysenvprovider);
        context.addProjectPropertyValue("maven.plugin.unpacked.dir", value);
        LocationFinderAggregator aggr = new LocationFinderAggregator(context);
        assertEquals(value, aggr.getMavenPluginsDir());
        context.addBuildPropertyValue("maven.home.local", "original home");
        context.addProjectPropertyValue("maven.plugin.unpacked.dir", "${maven.home.local}/plugs");
        assertEquals("original home/plugs", aggr.getMavenPluginsDir());
        value = "sysenv value";
        sysenvprovider.setMavenLocalHome(value);
        assertEquals(value + "/plugs", aggr.getMavenPluginsDir());
    }

    private class TestProvider implements SysEnvProvider {
        private String mavenHome;
        private String mavenLocalHome;
        private String javaHome;
        
        public String getProperty(String name) {
            if ("MAVEN_HOME".equals(name)) {
                return getMavenHome();
            }
            if ("JAVA_HOME".equals(name)) {
                return getJavaHome();
            }
            if ("MAVEN_HOME_LOCAL".equals(name)) {
                return getMavenLocalHome();
            }
            return null;
        }

        public String getMavenHome() {
            return mavenHome;
        }

        public void setMavenHome(String mavenHome) {
            this.mavenHome = mavenHome;
        }

        public String getMavenLocalHome() {
            return mavenLocalHome;
        }

        public void setMavenLocalHome(String mavenLocalHome) {
            this.mavenLocalHome = mavenLocalHome;
        }

        public String getJavaHome() {
            return javaHome;
        }

        public void setJavaHome(String javaHome) {
            this.javaHome = javaHome;
        }
        
    }
    
}
