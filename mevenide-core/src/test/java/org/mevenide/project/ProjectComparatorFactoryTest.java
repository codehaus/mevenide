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

import org.apache.maven.project.Project;

import junit.framework.TestCase;

/**
 * @author Jeffrey Bonevich <jeff@bonevich.com>
 * @version $Id$
 */
public class ProjectComparatorFactoryTest extends TestCase
{

	public void testGetComparator()
	{
		Project p1 = new Project();
		ProjectComparator comparator = ProjectComparatorFactory.getComparator(p1);
		assertNotNull(comparator);
	}

	public void testGetComparator_nullProject()
	{
		Project p1 = null;
		ProjectComparator comparator = ProjectComparatorFactory.getComparator(p1);
		assertNull(comparator);
	}

	public void testGetComparator_equivalentProjects()
	{
		Project p1 = new Project();
		p1.setId("test");
		Project p2 = new Project();
		p2.setId("test");
		
		ProjectComparator comparator1 = ProjectComparatorFactory.getComparator(p1);
		ProjectComparator comparator2 = ProjectComparatorFactory.getComparator(p2);
		assertTrue(comparator1 == comparator2);
	}

	public void testGetComparator_nonEquivalentProjects()
	{
		Project p1 = new Project();
		p1.setId("test");
		Project p2 = new Project();
		p2.setId("NOTtest");
		
		ProjectComparator comparator1 = ProjectComparatorFactory.getComparator(p1);
		ProjectComparator comparator2 = ProjectComparatorFactory.getComparator(p2);
		assertTrue(comparator1 != comparator2);
	}

	public void testUpdateComparator()
	{
		Project p1 = new Project();
		p1.setId("test");
		Project p2 = new Project();
		p2.setId("NOTtest");
		
		ProjectComparator comparator1 = ProjectComparatorFactory.getComparator(p1);

		ProjectComparatorFactory.updateComparator(p1, p2);
		
		// update should have reset the comparator map in the factory so we get the same
		// comparator instance
		ProjectComparator comparator2 = ProjectComparatorFactory.getComparator(p2);
		assertTrue(comparator1 == comparator2);
	}

}
