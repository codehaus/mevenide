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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomWriterTest extends TestCase {
	private PomWriter pomWriter;
	private File projectFile;

	protected void setUp() throws Exception {
		pomWriter = PomWriter.getWriter();
		File src = new File(PomWriterTest.class.getResource("/fixtures/project-fixture.xml").getFile());
		projectFile = new File(src.getParentFile().getParent(), "project-fixture.xml") ; 
		copy(src.getAbsolutePath(), projectFile.getAbsolutePath());
	}

	protected void tearDown() throws Exception {
		projectFile.delete();
	}

	public void testAddSource() throws Exception {
		pomWriter.addSource(
			"src/pyo/java",
			projectFile,
			BuildConstants.MAVEN_SRC);
		pomWriter.addSource(
			"src/pyo/aspect",
			projectFile,
			BuildConstants.MAVEN_ASPECT);
		Hashtable h = PomReader.getAllSourceDirectories(projectFile);
		assertEquals(2, h.size());
	}

	public void testAddDependency() {
	}

	private void copy(String sourceFile, String destFile) throws Exception {

		FileInputStream from = new FileInputStream(sourceFile);
		FileOutputStream to = new FileOutputStream(destFile);
		try {
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read);
			}
		} 
		finally {
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
		}

	}
}
