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
package org.mevenide.project;

import java.io.File;

import org.apache.maven.project.Project;
import org.mevenide.project.io.ProjectReader;

import junit.framework.TestCase;

/**
 * @author jbonevic
 * $Id$
 */
public class ProjectComparatorTest extends TestCase {

	static boolean changed = false;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

	static class MockListener implements IProjectChangeListener {
		public void projectChanged(ProjectChangeEvent e) {
			//System.out.println("Project changed: " + e.getPom() + " changed in " + e.getAttribute());
			changed = true;
		}
	}
	
	private static final MockListener LISTENER = new MockListener();

    public void testCompare_sameProject() {
		changed = false;
    	Project p1 = new Project();
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p1);
		assertTrue(!changed);
    }

	public void testCompare_nullProjects() {
		changed = false;
		Project p1 = null;
		Project p2 = null;
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(!changed);
	}

	public void testCompare_newProjectIsNull() {
		changed = false;
		Project p1 = new Project();
		Project p2 = null;
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(changed);
	}

	public void testCompare_originalProjectIsNull() {
		changed = false;
		Project p1 = null;
		Project p2 = new Project();
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(changed);
	}

    public void testCompare_emptyProjects() {
		changed = false;
    	Project p1 = new Project();
    	Project p2 = new Project();
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(!changed);
    }

	public void testCompare_nonNullEquivalentProjects() {
		changed = false;
		Project p1 = new Project();
		p1.setArtifactId("test1");
		p1.setGroupId("group1");
		Project p2 = new Project();
		p2.setArtifactId("test1");
		p2.setGroupId("group1");
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(!changed);
	}

	public void testCompare_nonNullNonEquivalentProjects() {
		changed = false;
		Project p1 = new Project();
		p1.setArtifactId("test1");
		p1.setGroupId("group1");
		Project p2 = new Project();
		p2.setArtifactId("test2");
		p2.setGroupId("group1");
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(changed);
	}

	public void testCompare_newHasNull() {
		changed = false;
		Project p1 = new Project();
		p1.setArtifactId("test1");
		p1.setGroupId("group1");
		Project p2 = new Project();
		p2.setArtifactId(null);
		p2.setGroupId("group1");
		ProjectComparator comparator = new ProjectComparator(p1);
		comparator.addProjectChangeListener(LISTENER);
		comparator.compare(p2);
		assertTrue(changed);
	}

	public void testCompare_readProjects() {
		try {
			changed = false;
			File pom = new File(ProjectComparatorTest.class.getResource("/project.xml").getFile());
			ProjectReader reader = ProjectReader.getReader();
			Project p1 = reader.read(pom);
			Project p2 = reader.read(pom);
			ProjectComparator comparator = new ProjectComparator(p1);
			comparator.addProjectChangeListener(LISTENER);
			comparator.compare(p2);
			assertTrue(!changed);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testCompare_readProjectsAndChange() {
		try {
			changed = false;
			File pom = new File(ProjectComparatorTest.class.getResource("/project.xml").getFile());
			ProjectReader reader = ProjectReader.getReader();
			Project p1 = reader.read(pom);
			Project p2 = reader.read(pom);
			p2.getBuild().setNagEmailAddress("bob@nospam.com");
			ProjectComparator comparator = new ProjectComparator(p1);
			comparator.addProjectChangeListener(LISTENER);
			comparator.compare(p2);
			assertTrue(changed);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
