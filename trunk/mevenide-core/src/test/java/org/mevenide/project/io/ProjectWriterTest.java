/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.project.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWriterTest extends AbstractMevenideTestCase {
	protected ProjectWriter pomWriter;
	protected DependencyFactory dependencyFactory;
	
	protected void setUp() throws Exception {
		super.setUp();
		pomWriter = ProjectWriter.getWriter();
		dependencyFactory = DependencyFactory.getFactory(); 
	}	

	public void testAddSource() throws Exception {
		pomWriter.addSource(
			"src/pyo/java",
			projectFile,
			ProjectConstants.MAVEN_SRC_DIRECTORY);
		pomWriter.addSource(
			"src/pyo/aspect",
			projectFile,
			ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		pomWriter.addSource(
			"src/test/java",
			projectFile,
			ProjectConstants.MAVEN_TEST_DIRECTORY);
		
		Map h = ProjectReader.getReader().getSourceDirectories(projectFile);
		
		assertEquals(3, h.size());
		
		assertTrue(h.containsValue("src/pyo/java"));
		assertTrue(h.containsValue("src/pyo/aspect"));
		assertTrue(h.containsValue("src/test/java"));
		
		assertEquals("src/pyo/java", h.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", h.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		assertEquals("src/pyo/aspect", h.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
	}

	public void testSetDependencies() throws Exception {
		Dependency dep = dependencyFactory.getDependency("E:/bleeeaaaah/testo/ploufs/testo-0.0.1.plouf");
		
		List l = new ArrayList();
		l.add(dep);
		
		pomWriter.setDependencies(l, projectFile);
		Project project = ProjectReader.getReader().read(projectFile);
		
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
	}
	
	public void testAddResource() throws Exception {
		pomWriter.addResource("src/conf", projectFile);
		assertTrue(isResourcePresent("src/conf", new String[] {"**/*.*"}));
		
		pomWriter.addResource("etc", projectFile);
		assertTrue(isResourcePresent("etc", new String[] {"**/*.*", "fake.xml"}));
		
		
	}

	public void testAddProject() throws Exception {
		File referencedPom = new File(ProjectWriterTest.class.getResource("/project.xml").getFile());
		
		pomWriter.addProject(referencedPom, projectFile);
		
		Project project = ProjectReader.getReader().read(projectFile);
		
		Dependency dep = dependencyFactory.getDependency("X:/bleah/mevenide/mevenide-core-1.0.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
	}

	private boolean isResourcePresent(String testDirectory, String[] includes) throws FileNotFoundException, Exception, IOException {
		Project project = ProjectReader.getReader().read(projectFile);
		List resources = project.getBuild().getResources();
		boolean found = false;
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = (Resource) resources.get(i);
			if ( resource.getDirectory() != null ) {
				boolean temp = resource.getDirectory().equals(testDirectory); 
				for (int j = 0; j < includes.length; j++) {
					temp &= resource.getIncludes().contains(includes[j]);
				} 
				if ( temp ) {
					found = true;	
				}
			}
		}
		return found;
	}

	public void testJarOverride() throws Exception {
		File propFile = new File(projectFile.getParent(), "project.properties");
		
		Project project = ProjectReader.getReader().read(projectFile);
		int prev = project.getDependencies().size();
		
		String path = "C:\\temp\\bleah\\fake.jar";
		String path2 = "C:\\temp\\bleah\\fake2.jar";
		String path3 = "C:\\temp space temp\\bleah\\fake fake2.jar";
		
		pomWriter.jarOverride(path, propFile, projectFile);
		pomWriter.jarOverride(path2, propFile, projectFile);
		pomWriter.jarOverride(path3, propFile, projectFile);
		pomWriter.jarOverride(path2, propFile, projectFile);
		
		project = ProjectReader.getReader().read(projectFile);
		
		assertEquals(prev + 3, project.getDependencies().size());
		
	}
	
	
	
}
