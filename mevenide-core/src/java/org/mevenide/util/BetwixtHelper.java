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
package org.mevenide.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.maven.project.Project;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class BetwixtHelper {
	public static Project readProject(File pom) throws Exception { 
		BeanReader reader = new BeanReader();
		reader.registerBeanClass("project", Project.class);
		FileReader fileReader = new FileReader(pom);
		Project project = (Project) reader.parse(fileReader);
		return project;
	}
	
	public static void writeProject(File pom, Project project) throws Exception {
		pom.delete();
		FileWriter fileWriter = new FileWriter(pom, true);
		BeanWriter writer = new BeanWriter(fileWriter);
		writer.enablePrettyPrint();
		//writer.setWriteEmptyElements(false);
		writer.getXMLIntrospector().setAttributesForPrimitives(false);
		writer.setWriteIDs(false);
		writer.writeXmlDeclaration("<?xml version=\"1.0\"?>");
		writer.write("project", project);
		writer.close();
		
	}
}
