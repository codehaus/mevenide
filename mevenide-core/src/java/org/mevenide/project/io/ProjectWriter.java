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
import java.io.FileWriter;
import java.io.Writer;

import org.apache.maven.project.Project;
import org.mevenide.ProjectConstants;
/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private ProjectReader projectReader ;
	private DefaultProjectMarshaller marshaller ; 
	
	private static ProjectWriter projectWriter = null;
	private static Object lock = new Object();
	
	
	private ProjectWriter() throws Exception  {
		marshaller = new DefaultProjectMarshaller();
	}
	
	public static ProjectWriter getWriter() throws Exception {
		if (projectWriter != null) {
			return projectWriter;
		}
		else {
			synchronized (lock) {
				if (projectWriter == null) {
					projectWriter = new ProjectWriter();
				}
			}
			return projectWriter;
		}
	}
	
	public void addSource(String path, File pom, String sourceType) throws Exception {
		
		Project project = projectReader.readProject(pom);
		
		if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setAspectSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setUnitTestSourceDirectory(path);
		}
		
		Writer writer = new FileWriter(pom, false);
		marshaller.marshall(writer, project);
		writer.close();
	}

	
	
	public void addDependency(String absoluteFileName, File pom) throws Exception {
		
	}
	
	public String getSkeleton(String projectName) throws Exception {
		return ProjectSkeleton.getSkeleton(projectName);
	}
	
	

}
