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
package org.mevenide.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.mevenide.project.DependencyFactory;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.project.io.ProjectWriterTest;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class AbstractMevenideTestCase extends TestCase {
	protected ProjectWriter pomWriter;
	protected File projectFile;
	protected DependencyFactory dependencyFactory;

	protected void setUp() throws Exception {
		pomWriter = ProjectWriter.getWriter();
		File src = new File(ProjectWriterTest.class.getResource("/fixtures/project-fixture.xml").getFile());
		projectFile = new File(src.getParentFile().getParent(), "project-fixture.xml") ; 
		copy(src.getAbsolutePath(), projectFile.getAbsolutePath());
		dependencyFactory = DependencyFactory.getFactory();
	
	}

	protected void tearDown() throws Exception {
		projectFile.delete();
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
