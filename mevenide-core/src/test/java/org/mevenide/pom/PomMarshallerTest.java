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

import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.project.Project;
import org.apache.maven.project.builder.DefaultProjectUnmarshaller;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomMarshallerTest extends TestCase {

	private Writer writer;
	private Project project;
	private DefaultProjectMarshaller marshaller;
	
	protected void setUp() throws Exception {
		writer = new StringWriter();
		String pomFile = PomMarshallerTest.class.getResource("/marshall-project.xml").getFile();
		Reader reader = new FileReader(pomFile);
		project = new DefaultProjectUnmarshaller().parse(reader);
		marshaller = new DefaultProjectMarshaller();
	}

	protected void tearDown() throws Exception {
		writer = null;
	}

	public void testMarshall() throws Exception {
		marshaller.marshall(writer, project);
		System.out.print(writer.toString());
	}

}
