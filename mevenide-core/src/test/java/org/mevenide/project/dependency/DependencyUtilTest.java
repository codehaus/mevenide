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

    private Dependency dependency_one_groupone_1_0;
    private Dependency dependency_one_groupone_null;
    private Dependency dependency_one_groupone_1_0_;
    private Dependency dependency_one_grouptwo_1_0;
    private Dependency dependency_two_groupone_1_0;
	
	protected void setUp() throws Exception {
		super.setUp();
		Environment.setMavenRepository("C:\\Documents and Settings\\gdodinet.MCCAIN-1\\.maven\\repository");
		dependencyFactory = DependencyFactory.getFactory();
		initDependencies();
		
	}
		
	protected void tearDown() throws Exception {
        super.tearDown();
        dependency_one_groupone_1_0 = null;
		dependency_one_groupone_null = null;
		dependency_one_groupone_1_0_ = null;
		dependency_one_grouptwo_1_0 = null;
		dependency_two_groupone_1_0 = null;
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
	
	public void testAreEquals() {
		assertTrue(DependencyUtil.areEquals("AaaC", "AaaC"));
		assertTrue(!DependencyUtil.areEquals("AaaC", null));
		assertTrue(!DependencyUtil.areEquals(null, "AaaC"));
		assertTrue(!DependencyUtil.areEquals("AaC", "AaaC"));
	}

	public void testAreEqualsD() {
		assertTrue(DependencyUtil.areEquals(dependency_one_groupone_1_0, dependency_one_groupone_1_0));
		assertTrue(DependencyUtil.areEquals(dependency_one_groupone_1_0, dependency_one_groupone_1_0_));
		assertTrue(DependencyUtil.areEquals(dependency_one_groupone_1_0_, dependency_one_groupone_1_0));
		assertTrue(!DependencyUtil.areEquals(dependency_one_groupone_1_0, null));
		assertTrue(!DependencyUtil.areEquals(null, dependency_one_groupone_1_0));
		assertTrue(!DependencyUtil.areEquals(dependency_one_groupone_1_0, dependency_one_groupone_null));
		assertTrue(!DependencyUtil.areEquals(dependency_one_groupone_1_0, dependency_one_grouptwo_1_0));
		assertTrue(!DependencyUtil.areEquals(dependency_one_groupone_1_0, dependency_two_groupone_1_0));	
	}
	
	public void testConflict() {
		assertTrue(!DependencyUtil.conflict(dependency_one_groupone_1_0, dependency_one_groupone_1_0));
		assertTrue(!DependencyUtil.conflict(dependency_one_groupone_1_0, null));
		assertTrue(!DependencyUtil.conflict(null, dependency_one_groupone_1_0));
		assertTrue(!DependencyUtil.conflict(dependency_one_groupone_1_0, dependency_one_groupone_1_0_));
		assertTrue(!DependencyUtil.conflict(dependency_one_grouptwo_1_0, dependency_one_groupone_1_0));
		assertTrue(DependencyUtil.conflict(dependency_one_groupone_1_0, dependency_one_groupone_null));
		assertTrue(!DependencyUtil.conflict(dependency_one_groupone_1_0, dependency_one_grouptwo_1_0));
		assertTrue(!DependencyUtil.conflict(dependency_one_groupone_1_0, dependency_two_groupone_1_0));	
	}

	private void initDependencies() {
        dependency_one_groupone_1_0 = new Dependency();
        dependency_one_groupone_1_0.setArtifactId("one");
		dependency_one_groupone_1_0.setGroupId("groupone");
		dependency_one_groupone_1_0.setVersion("1.0");
		
		dependency_one_groupone_1_0_ = new Dependency();
        dependency_one_groupone_1_0_.setArtifactId("one");
		dependency_one_groupone_1_0_.setGroupId("groupone");
		dependency_one_groupone_1_0_.setVersion("1.0");

		dependency_one_groupone_null = new Dependency();
        dependency_one_groupone_null.setArtifactId("one");
		dependency_one_groupone_null.setGroupId("groupone");
		dependency_one_groupone_null.setVersion(null);
		
		dependency_one_grouptwo_1_0 = new Dependency();
        dependency_one_grouptwo_1_0.setArtifactId("one");
		dependency_one_grouptwo_1_0.setGroupId("grouptwo");
		dependency_one_grouptwo_1_0.setVersion("1.0");
		
		dependency_two_groupone_1_0 = new Dependency();
        dependency_two_groupone_1_0.setArtifactId("two");
		dependency_two_groupone_1_0.setGroupId("groupone");
		dependency_two_groupone_1_0.setVersion("1.0");
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
