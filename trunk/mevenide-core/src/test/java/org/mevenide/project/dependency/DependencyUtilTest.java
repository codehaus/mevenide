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
package org.mevenide.project.dependency;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.Environment;
import org.mevenide.project.io.ProjectWriterTest;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyUtilTest extends AbstractMevenideTestCase {
	
	protected DependencyFactory dependencyFactory;
	
	protected void setUp() throws Exception {
		super.setUp();
		Environment.setMavenRepository("C:\\Documents and Settings\\gdodinet.MCCAIN-1\\.maven\\repository");
		dependencyFactory = DependencyFactory.getFactory();
		
	}
		
	public void testIsDependencyPresent()throws Exception {
		Project project = new DefaultProjectUnmarshaller().parse(new FileReader(projectFile));
		List dependencies = project.getDependencies();
		
		Dependency dep = dependencyFactory.getDependency("E:/maven/repository/junit/jars/junit-3.8.1.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/junit/jars/junit-3.8.1.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
		 
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/jars/junit-3.8.1.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
		
		dep = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/junit-3.8.1.jar");
		assertTrue(DependencyUtil.isDependencyPresent(project, dep));
		
	}
	
	

	


	public void testAreEqualsD() {
		Dependency d1 = new Dependency();
		d1.setArtifactId("one");
		d1.setGroupId("groupone");
		d1.setVersion("1.0");
		
		Dependency d2 = new Dependency();
		d2.setArtifactId("one");
		d2.setGroupId("groupone");
		d2.setVersion(null);
		
		assertTrue(DependencyUtil.areEquals(d1, d1));
		assertTrue(!DependencyUtil.areEquals(d1, null));
		assertTrue(!DependencyUtil.areEquals(null, d1));
		assertTrue(!DependencyUtil.areEquals(d1, d2));
		assertTrue(!DependencyUtil.areEquals(d2, d1));
		
	}
	
	public void testIsValid() throws Exception {
		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\jtestcase\\lib\\xtype.jar")));
		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\clover-1.2\\lib\\clover.jar")));
		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\jtestcase\\lib\\xmlutil.jar")));
		Dependency d = new Dependency();
		d.setGroupId("rtt");
		d.setArtifactId("rtt");
		d.setVersion("5.0");
		assertTrue(DependencyUtil.isValid(d));
	}
	
	public void testGetNonResolvedDependencies() throws Exception {
		List deps = new ArrayList();
	
		Dependency d1 = new Dependency();
		deps.add(d1);
	
		Dependency d2 = DependencyFactory.getFactory().getDependency(ProjectWriterTest.class.getResource("/my-0.3.txt").getFile());
		deps.add(d2);
	
		Dependency d3 = new Dependency();
		d3.setGroupId("rtt");
		d3.setArtifactId("rtt");
		d3.setVersion("5.0");
		deps.add(d3);
	
		Dependency d4 = new Dependency();
		d4.setGroupId("rtt");
		deps.add(d4);
	
		List ds = DependencyUtil.getNonResolvedDependencies(deps);
	
		assertEquals(1, deps.size());
		assertEquals(3, ds.size());
	}
}
