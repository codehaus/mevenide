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

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: JarOverriderTest.java,v 1.1 24 sept. 2003 Exp gdodinet 
 * 
 */
public class JarOverrideWriterTest  extends AbstractMevenideTestCase {
	private JarOverrideWriter overrider ;
	private ProjectWriter pomWriter;
	
    protected void setUp() throws Exception {
        super.setUp();
		pomWriter = ProjectWriter.getWriter();
        overrider = new JarOverrideWriter(pomWriter);
    }
	
	protected void tearDown() throws Exception {
		super.tearDown();
		pomWriter = null;
		overrider = null;
    }
	
	public void testJarOverride() throws Exception {
		File propFile = new File(projectFile.getParent(), "project.properties");
	
		Project project = ProjectReader.getReader().read(projectFile);
		int prev = project.getDependencies().size();
	
		String path = "C:\\temp\\bleah\\fake.jar";
		String path2 = "C:\\temp\\bleah\\fake2.jar";
		String path3 = "C:\\temp space temp\\bleah\\fake fake2.jar";
	
		Dependency dep = new Dependency();
		dep.setJar(path);
		dep.setArtifactId("fake");
		
		Dependency dep2 = new Dependency();
		dep2.setJar(path2);
		dep2.setArtifactId("fake2");
		
		Dependency dep3 = new Dependency();
		dep3.setJar(path3);
		dep3.setArtifactId("fake fake2");
		
		overrider.jarOverride(dep.getArtifactId(), path, project);
		overrider.jarOverride(dep2.getArtifactId(), path2, project);
		overrider.jarOverride(dep3.getArtifactId(), path3, project);
		overrider.jarOverride(dep2.getArtifactId(), path2, project);
	
		PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(new File(projectFile.getParentFile(), "project.properties"));
//		System.err.println(model.toString());
//		assertEquals(path, model.findByKey("maven.jar." + dep.getArtifactId()).getValue());
//		assertEquals(path2, model.findByKey("maven.jar." + dep2.getArtifactId()).getValue());
//		assertEquals(path3, model.findByKey("maven.jar." + dep3.getArtifactId()).getValue());
	
	}
}
