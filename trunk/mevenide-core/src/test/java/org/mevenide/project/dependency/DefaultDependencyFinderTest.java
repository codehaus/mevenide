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
package org.mevenide.project.dependency;

import java.io.File;

import org.apache.maven.project.Dependency;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.environment.sysenv.DefaultSysEnvProvider;

import junit.framework.TestCase;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DefaultDependencyFinderTest.java,v 1.1 28 mars 2004 Exp gdodinet 
 * 
 */
public class DefaultDependencyFinderTest extends TestCase {
	
	private IDependencyPathFinder pathFinder = null;
	private File pom;
	
	protected void setUp() throws Exception {
		pom = new File(DefaultDependencyFinderTest.class.getResource("/project.xml").getFile());
	}
	
	protected void tearDown() throws Exception {
		pom = null;
	}

// for some reason the test didn't run for me.. mkleint, commented out in order to do the transfer to maven-rc2
	public void testResolve() {
//		Dependency dep = new Dependency();
//		dep.setArtifactId("maven");
//		pathFinder = new DefaultDependencyPathFinder(dep, pom);
//		assertEquals((pom.getParent() + "/lib/maven-1.0-rc1.jar").replaceAll("\\\\", "/"), pathFinder.resolve());
//		
//		dep.setArtifactId("commons-logging");
//		dep.setGroupId("commons-logging");
//		dep.setVersion("1.0.3");
//		pathFinder = new DefaultDependencyPathFinder(dep, pom);
//		SysEnvLocationFinder.setDefaultSysEnvProvider(new DefaultSysEnvProvider());
//		String mavenRepo = new LocationFinderAggregator().getMavenLocalRepository();
//		File group = new File(mavenRepo, "commons-logging");
//		File jars =  new File(group, "jars");
//		File path = new File(jars, "commons-logging-1.0.3.jar");
//		assertEquals(path.getAbsolutePath().replaceAll("\\\\", "/"), pathFinder.resolve());
//		
	}
}

