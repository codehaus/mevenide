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
 * 
 */
package org.mevenide.pom;

import java.io.File;
import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomReaderTest extends TestCase {

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAllSourceDirectories() throws Exception {
		Hashtable map = PomReader.getAllSourceDirectories(new File(PomReaderTest.class.getResource("/project.xml").getFile()));
		assertEquals(BuildConstants.MAVEN_SRC, (String)map.get("${basedir}/src/java"));	
		assertEquals(BuildConstants.MAVEN_TEST, (String)map.get("${basedir}/src/test/java"));
		assertEquals(BuildConstants.MAVEN_ASPECT, (String)map.get("${basedir}/src/aspects"));	
	}	

}
