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
package org.mevenide.project.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.maven.project.Project;
import org.apache.maven.project.builder.DefaultProjectUnmarshaller;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectSkeleton {
	private static String template = "/templates/standard/project.xml";
	
	private ProjectSkeleton() {
	}

	/**
	 * 
	 * @param projectName
	 * @return
	 * @throws Exception
	 */
	public static String getSkeleton(String projectName) throws Exception {
		
		InputStream is = ProjectSkeleton.class.getResourceAsStream(template);
		
		Reader reader = new InputStreamReader(is);
		Project project = new DefaultProjectUnmarshaller().parse(reader);
		reader.close();
		
		project.setId(projectName.toLowerCase());
		project.setName(projectName);
		project.setInceptionYear(getCurrentYear());
		
		Writer writer = new StringWriter();
		new DefaultProjectMarshaller().marshall(writer, project);
		writer.close();
		return writer.toString();
	}

	private static String getCurrentYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		return Integer.toString(calendar.get(GregorianCalendar.YEAR));
	}
}
	
	