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
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.maven.project.Dependency;

import junit.framework.TestCase;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.LocationFinderAggregator;

/**
 *
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class JarOverrideReader2Test extends TestCase {
    private File userHomeDir;
    private File projectDir;
    private String originalUserHome;
    private IQueryContext context;
    private File pom;
    private Dependency commonsDiscoveryDependency;
    private Dependency mavenDependency;
    private File expectedDiscoveryPath;
    
    public JarOverrideReader2Test() {
        originalUserHome = System.getProperty("user.home"); //NOI18N
        userHomeDir  = new File(originalUserHome, ".mevenide_test");
        
    }
    
    protected void setUp() throws Exception {
        System.setProperty("user.home", userHomeDir.getAbsolutePath());
        if (!userHomeDir.exists()) {
            userHomeDir.mkdir();
        }
        File userprop = new File(JarOverrideReader2Test.class.getResource("/build.properties").getFile());
        File copyTo = new File(userHomeDir, "build.properties");
        copy(userprop.getAbsolutePath(), copyTo.getAbsolutePath());
        projectDir = new File(userHomeDir, "test_project");
        if (!projectDir.exists()) {
            projectDir.mkdir();
        }
        
        File projectprop = new File(JarOverrideReader2Test.class.getResource("/project.properties").getFile());
        copyTo = new File(projectDir, "project.properties");
        copy(projectprop.getAbsolutePath(), copyTo.getAbsolutePath());
        
        File pom = new File(JarOverrideReader2Test.class.getResource("/project.xml").getFile());
        copyTo = new File(projectDir, "project.xml");
        copy(pom.getAbsolutePath(), copyTo.getAbsolutePath());
        
        context = new DefaultQueryContext(projectDir);
        commonsDiscoveryDependency = new Dependency();
        mavenDependency = new Dependency();
        
        commonsDiscoveryDependency.setGroupId("commons-discovery");
        commonsDiscoveryDependency.setArtifactId("commons-discovery");
        commonsDiscoveryDependency.setVersion("0.1");
        
        mavenDependency.setGroupId("maven");
        mavenDependency.setArtifactId("maven");
        mavenDependency.setVersion("SNAPSHOT");
        LocationFinderAggregator finder = new LocationFinderAggregator(context);
        File discoveryRepoPath = new File(finder.getMavenLocalRepository(), "commons-discovery");
        File discoveryTypePath = new File(discoveryRepoPath, "jars");
        expectedDiscoveryPath = new File(discoveryTypePath, "commons-discovery-0.2.jar");
        
    }
    
    protected void tearDown() throws Exception {
        delete(userHomeDir);
        System.setProperty("user.home", originalUserHome);
    }
    
    public void testDependencyOverrideValue() {
        String mavenOverrideValue = JarOverrideReader2.getInstance().processOverride(mavenDependency, context);
        assertEquals("lib/maven-1.0-rc1.jar", mavenOverrideValue);
        
        String discoveryOverrideValue = JarOverrideReader2.getInstance().processOverride(commonsDiscoveryDependency, context);
        assertEquals(expectedDiscoveryPath.getAbsolutePath(), discoveryOverrideValue);
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
    
    
    protected void copy(String sourceFile, String destFile) throws Exception {
        
        FileInputStream from = new FileInputStream(sourceFile);
        FileOutputStream to = new FileOutputStream(destFile);
        try {
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytes_read);
            }
        }
        finally {
            if (from != null) {
                from.close();
            }
            if (to != null) {
                to.close();
            }
        }
        
    }
}
