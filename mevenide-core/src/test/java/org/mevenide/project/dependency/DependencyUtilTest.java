/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
