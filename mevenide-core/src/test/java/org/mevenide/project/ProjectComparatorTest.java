/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich.  All rights
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
