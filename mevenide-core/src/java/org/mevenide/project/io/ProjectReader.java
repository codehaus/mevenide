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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.builder.DefaultProjectUnmarshaller;
import org.jdom.input.SAXBuilder;
import org.mevenide.ProjectConstants;


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
	
	/**
	 * returns all source directories specified in the POM.
	 * returns a Map whose keys are the src directories type (src, test, aspects, integration) 
	 * and whose entries are the absolute path of the src dirs.
	 * @param pom
	 * @return
	 * @throws Exception
	 */
	public Map getSourceDirectories(File pom) throws Exception {
		Map sourceDirectories = new HashMap();
		
		Build build = getBuild(pom);
		
		String aspectSourceDirectory = build.getAspectSourceDirectory();
		if ( !isNull(aspectSourceDirectory)) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_ASPECT_DIRECTORY,
				aspectSourceDirectory
			);
		}
		
		String sourceDirectory = build.getSourceDirectory();
		if ( !isNull(sourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_SRC_DIRECTORY,
				sourceDirectory
			);	
		}
		
		String unitTestSourceDirectory = build.getUnitTestSourceDirectory();
		if ( !isNull(unitTestSourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_TEST_DIRECTORY,
				unitTestSourceDirectory
			);
		}
		
		String integrationUnitTestSourceDirectory = build.getIntegrationUnitTestSourceDirectory();
		if ( !isNull(integrationUnitTestSourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY,
				integrationUnitTestSourceDirectory
			);	
		}
		
		return sourceDirectories;
	}

	/** 
	 * checks if a given path is a valid directory 
	 * 
	 * @param sourceDirectory
	 * @return
	 */
	private boolean isNull(String sourceDirectory) {
		return sourceDirectory == null 
		 		|| sourceDirectory.trim().equals("");
	}

	/**
	 * utility method tha allows some factorization
	 * 
	 * @param pom
	 * @return
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	private static Build getBuild(File pom)
		throws Exception, FileNotFoundException {
		Project project; 
		if ( pom != null ) {
			project = new DefaultProjectUnmarshaller().parse(new FileReader(pom));
		}		
		else {
			project = new Project();
		}
		
		Build build = project.getBuild();
		return build;
	}
	
	public Dependency getDependency(File referencedPom) throws FileNotFoundException, Exception, IOException {
		Project referencedProject = read(referencedPom);
		Dependency dependency = new Dependency();
		dependency.setGroupId(referencedProject.getGroupId());
		dependency.setArtifactId(referencedProject.getArtifactId());
		dependency.setVersion(referencedProject.getCurrentVersion());
		return dependency;
	}
	/**
	 * @deprecated no replacement 
	 * 
	 * still used in org.mevenide.ui.eclipse.sync.pom.PomSynchronizer
	 * 
	 * @param pom
	 * @return
	 */
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
