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
package org.mevenide.pom;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.TestCase;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PomHelperTest extends TestCase {
	public PomHelperTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
	}
	
	protected void tearDown() throws Exception {
	}
	
	public void testGetSourceDirectories() throws Exception {
		assertEquals(2, PomReader.getAllSourceDirectories(createTmpGoalFile("pom")).size());
	}
	
	private File createTmpGoalFile(String xmlOutputFilename) throws Exception {
		String outputString = 
			"<root>" +
			"	<build>" +
			"		<sourceDirectory>src/java</sourceDirectory>" +
			"		<unitTestSourceDirectory>src/test</unitTestSourceDirectory>" +
			"	</build>" +
			"</root>";
		File output = File.createTempFile("tmp", xmlOutputFilename);
		FileOutputStream fileOutputStream = new FileOutputStream(output);
		fileOutputStream.write(outputString.getBytes());
		return output;
	}
}
