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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.util.DefaultProjectUnmarshaller;

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
		
		project.setDependencies(null);
		assertFalse(DependencyUtil.isDependencyPresent(project, dep));

		project.setDependencies(new ArrayList());
		
		project.addDependency(dep);
		Dependency dep2 = dependencyFactory.getDependency("E:/bleeeaaaah/plouf/jars/junit-3.7.jar");
		assertFalse(DependencyUtil.isDependencyPresent(project, dep2));
		
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

//	In maven-rc2, the Dependency.getId() requires the Dependency to have either id, or artifactid/groupid set.
//  kind of like DependencyUtils.isValid() however it throws IllegalStateException,
//  effectively rendering the isValid() obsolete and non working.
//  -- mkleint      
    public void testIsValid() throws Exception {
//		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\jtestcase\\lib\\xtype.jar")));
//		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\clover-1.2\\lib\\clover.jar")));
//		assertFalse(DependencyUtil.isValid(DependencyFactory.getFactory().getDependency("E:\\jtestcase\\lib\\xmlutil.jar")));
		Dependency d = new Dependency();
		d.setGroupId("rtt");
		d.setArtifactId("rtt");
		d.setVersion("5.0");
		assertTrue(DependencyUtil.isValid(d));
		
		assertFalse(DependencyUtil.isValid(null));
	}

//	In maven-rc2, the Dependency.getId() requires the dependnecy to have either id, or artifactid/groupid set.
//  kind of like DependencyUtils.isValid() however it throws IllegalStateException,
//  effectively rendering the getUnresolvedDependencies() obsolete and non working.
//  -- mkleint  
//	public void testGetNonResolvedDependencies() throws Exception {
//		List deps = new ArrayList();
//	
//		Dependency d1 = new Dependency();
//		deps.add(d1);
//	
//		Dependency d2 = DependencyFactory.getFactory().getDependency(ProjectWriterTest.class.getResource("/my-0.3.txt").getFile());
//		deps.add(d2);
//	
//		Dependency d3 = new Dependency();
//		d3.setGroupId("rtt");
//		d3.setArtifactId("rtt");
//		d3.setVersion("5.0");
//		deps.add(d3);
//	
//		Dependency d4 = new Dependency();
//		d4.setGroupId("rtt");
//		deps.add(d4);
//	
//		List ds = DependencyUtil.getNonResolvedDependencies(deps);
//	
//		assertEquals(1, deps.size());
//		assertEquals(3, ds.size());
//	}
}
