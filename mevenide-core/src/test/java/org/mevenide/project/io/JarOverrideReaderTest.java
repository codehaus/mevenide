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
package org.mevenide.project.io;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.maven.project.Dependency;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

import junit.framework.TestCase;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: JarOverrideReaderTest.java,v 1.1 13 mars 2004 Exp gdodinet 
 * 
 */
public class JarOverrideReaderTest extends TestCase {
	private File pom;
	private JarOverrideReader jarOverrideReader;
	
	private PropertyModel projectProperties;
	private PropertyModel buildProperties;
	
	private ILocationFinder locationFinder;
	
	private Dependency commonsDiscoveryDependency;
	private Dependency mavenDependency;
	
	private File expectedDiscoveryPath ;
	
	protected void setUp() throws Exception {
		pom = new File(ProjectReaderTest.class.getResource("/project.xml").getFile());
		setUpProperties(pom);
		jarOverrideReader = new JarOverrideReader();
		
		PropertyModelFactory projectModelFactory = PropertyModelFactory.getFactory(); 
		projectProperties = projectModelFactory.newPropertyModel(new File(pom.getParent(), "project.properties"), false);
		buildProperties = projectModelFactory.newPropertyModel(new File(pom.getParent(), "build.properties"), false);

		locationFinder = new LocationFinderAggregator();
		
		commonsDiscoveryDependency = new Dependency();
		mavenDependency = new Dependency(); 
		
		commonsDiscoveryDependency.setGroupId("commons-discovery");
		commonsDiscoveryDependency.setArtifactId("commons-discovery");
		commonsDiscoveryDependency.setVersion("0.1");
		
		mavenDependency.setGroupId("maven");
		mavenDependency.setArtifactId("maven");
		mavenDependency.setVersion("SNAPSHOT");
		
		File discoveryRepoPath = new File(locationFinder.getMavenLocalRepository(), "commons-discovery");
		File discoveryTypePath = new File(discoveryRepoPath, "jars");
		expectedDiscoveryPath = new File(discoveryTypePath, "commons-discovery-0.2.jar");
	}
	
	private void setUpProperties(File pom) throws Exception {
		FileOutputStream fos = null;
		try  {
			String testContent = "#test project.properties " + "\r\n" 
								+ "maven.jar.override = on "  + "\r\n"
								+ "maven.jar.commons-discovery = 0.2 " + "\r\n"
								+ "maven.jar.maven = lib/maven-1.0-rc1.jar";

			File projectProperties = new File(pom.getParent(), "project.properties");
			fos = new FileOutputStream(projectProperties, false);
			fos.write(testContent.getBytes());
		}
		finally {
			if ( fos != null ) {
				fos.close();
			}
		}
	}
	
	protected void tearDown() throws Exception {
		pom = null;
		projectProperties = null;
		buildProperties = null;
	}
	
	public void testIsJarOverrideOn() throws Exception {
		boolean isJarOverrideOn = jarOverrideReader.isJarOverrideOn(pom);
		
		assertEquals(true, isJarOverrideOn);
	}
	
	public void testDependencyOverrideValue() {
		String mavenOverrideValue = jarOverrideReader.getDependencyOverrideValue("maven", projectProperties, buildProperties);
		assertEquals("lib/maven-1.0-rc1.jar", mavenOverrideValue);
		
		String discoveryOverrideValue = jarOverrideReader.getDependencyOverrideValue("commons-discovery", projectProperties, buildProperties);
		assertEquals("0.2", discoveryOverrideValue);	
	}
	
	public void testGetVersionOverrideValue() throws Exception {
		String discoveryOverrideValue = jarOverrideReader.getVersionOverrideValue(pom, commonsDiscoveryDependency, "0.2");
		assertEquals(expectedDiscoveryPath.getAbsolutePath(), discoveryOverrideValue);
		
		String mavenOverrideValue = jarOverrideReader.getVersionOverrideValue(pom, mavenDependency, "lib/maven-1.0-rc1");
		assertNull(mavenOverrideValue);
	}
		
	public void testGetOverrideValue() throws Exception {
		String discoveryOverrideValue = jarOverrideReader.getOverrideValue(pom, commonsDiscoveryDependency);
		assertEquals(expectedDiscoveryPath.getAbsolutePath(), discoveryOverrideValue);
		
		String mavenOverrideValue = jarOverrideReader.getOverrideValue(pom, mavenDependency);
		assertEquals("lib/maven-1.0-rc1.jar", mavenOverrideValue);
	}
}
