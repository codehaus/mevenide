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
package org.mevenide.project.dependency;

import java.io.File;

import org.apache.maven.project.Dependency;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.environment.ConfigUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyFactoryTest extends AbstractMevenideTestCase{
	
	
	private File artefact;
	private DependencyFactory dependencyFactory;

	private File testTypeDirectory;

    protected void setUp() throws Exception {
    	super.setUp();
        String mavenRepo = ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository();
		File testArtifactDirectory = new File(mavenRepo, "mevenide"); 
		testTypeDirectory = new File(testArtifactDirectory, "txts");
		testTypeDirectory.mkdirs();
		dependencyFactory = DependencyFactory.getFactory();
    }


    protected void tearDown() throws Exception {
    	super.tearDown();
    	dependencyFactory = null;
    }
	
	public void testGetDependency() throws Exception {
		
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-dev.txt");
		Dependency dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("mevenide", dep.getGroupId());
		
//		Environment.setMavenHome(System.getProperty("user.home"));
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		
		assertEquals("mevenide", dep.getGroupId());
		assertEquals("1.0.7-dev", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.7-beta1", dep.getVersion());
//		assertEquals("foo+joe-test2.-bar-1.0.7-beta1.txt", dep.getJar());
		
		artefact = new File(testTypeDirectory, "junit-3.8.1.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("3.8.1", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta-1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencySplitter_split-DEP_PATTERN $DEP-1
		assertEquals("1.0.7-beta-1", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "junit-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File("c:/jdk1.4.1/jre/lib/rt.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencyResolver_getDependency-NOT_RECOGNIZED_PATTERN $DEP-2
		assertNull(dep.getVersion());	
		assertEquals("rt", dep.getArtifactId());
		//groupId is null => setting it to artifactId
		assertEquals("rt", dep.getGroupId());
		assertEquals("rt.jar", dep.getJar());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3-SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
//		assertEquals("ojb-SNAPSHOT.pyo", dep.getJar());
		
		artefact = new File(testTypeDirectory, "testo-0.0.1.plouf");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("0.0.1", dep.getVersion());
		assertEquals("testo", dep.getArtifactId());
		
		
		File httpClientRepo = new File(ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository(), "commons-httpclient");
		File jarDir = new File(httpClientRepo, "jars");
		jarDir.mkdirs();
		dep = dependencyFactory.getDependency(new File(jarDir, "commons-httpclient-2.0alpha1-20020829.jar").getAbsolutePath());
		assertEquals("2.0alpha1-20020829", dep.getVersion());
		assertEquals("commons-httpclient", dep.getArtifactId());
		assertEquals("commons-httpclient", dep.getGroupId());
		
		dep = dependencyFactory.getDependency("/home/my-fake-0.1.zip");
		assertEquals("0.1", dep.getVersion());
		assertEquals("my-fake", dep.getArtifactId());
		//groupId is null => setting it to artifactId
		assertEquals("my-fake", dep.getGroupId());
//		assertEquals("my-fake-0.1.zip", dep.getJar());
	}

	
}
