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
package org.mevenide.project;

import org.apache.maven.project.Dependency;

import junit.framework.TestCase;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyUtilTest extends TestCase {


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
	
	
}
