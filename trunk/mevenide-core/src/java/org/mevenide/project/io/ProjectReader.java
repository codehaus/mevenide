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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.maven.project.Project;
import org.apache.maven.project.builder.DefaultProjectUnmarshaller;
import org.jdom.input.SAXBuilder;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectReader {
	private DefaultProjectUnmarshaller unmarshaller ; 
	
	private static ProjectReader projectReader = null;
	private static Object lock = new Object();
	
	public static ProjectReader getReader() throws Exception {
		if (projectReader != null) {
			return projectReader;
		}
		else {
			synchronized (lock) {
				if (projectReader == null) {
					projectReader = new ProjectReader();
				}
			}
			return projectReader;
		}
	}
	
	private ProjectReader() {
		unmarshaller = new DefaultProjectUnmarshaller(); 
	}
	
	public Project read(File pom) throws FileNotFoundException, Exception, IOException {
		Reader reader = new FileReader(pom);
		Project project = unmarshaller.parse(reader);
		reader.close();
		return project;
	}
	
//	public static Hashtable getAllSourceDirectories(File pom) throws Exception {
//		Hashtable srcDirs = new Hashtable();
//		Project project; 
//		
//		if ( pom != null ) {
//			project = BetwixtHelper.readProject(pom);
//		}		
//		else {
//			project = new Project();
//		}
//		
//		if ( project.getBuild().getAspectSourceDirectory() != null 
//		 	&& !project.getBuild().getAspectSourceDirectory().trim().equals("")) {
//			srcDirs.put(project.getBuild().getAspectSourceDirectory(), BuildConstants.MAVEN_ASPECT);
//		}
//		if ( project.getBuild().getSourceDirectory() != null 
//			&& !project.getBuild().getSourceDirectory().trim().equals("")) {
//			srcDirs.put(project.getBuild().getSourceDirectory(), BuildConstants.MAVEN_SRC);	
//		}
//		if ( project.getBuild().getUnitTestSourceDirectory() != null 
//			&& !project.getBuild().getUnitTestSourceDirectory().trim().equals("")) {
//			srcDirs.put(project.getBuild().getUnitTestSourceDirectory(), BuildConstants.MAVEN_TEST);
//		}
//		//srcDirs.put(project.getBuild().getUnitTestSourceDirectory()
//		return srcDirs;
//	}
	
	public static boolean isWellFormed(File pom) {
		try {
			new SAXBuilder().build(pom);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
}
