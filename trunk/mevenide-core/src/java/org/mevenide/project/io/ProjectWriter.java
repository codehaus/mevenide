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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.ProjectConstants;
import org.mevenide.project.DependencyUtil;

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
		projectReader = ProjectReader.getReader();
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
	
	/**
	 * add a resource entry to the pom (more precisely to the build element). 
	 * the resource is expected to be a directory, however we will 
	 * handle the case where it is a single file
	 * 
	 * @param path
	 * @param pom
	 */
	public void addResource(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		Resource resource = newResource(path);
		mergeSimilarResources(project, resource);
		write(project, pom);	
	}
	
	/**
	 * iterate ${pom.build.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
	private void mergeSimilarResources(Project project, Resource resource) {
		List similar = new ArrayList();
		
		List resources = project.getBuild().getResources();
		
		for (int i = 0; i < resources.size(); i++) {
			Resource declaredResource = (Resource) resources.get(i);
			if ( declaredResource.getDirectory().equals(resource.getDirectory()) ) {
				similar.add(declaredResource);
			}
		}
		
		for (int i = 0; i < similar.size(); i++) {
			Resource similarResource = (Resource) similar.get(i);
			resource.getIncludes().addAll(similarResource.getIncludes());
			resource.getExcludes().addAll(similarResource.getExcludes());
			project.getBuild().getResources().remove(similarResource);
		}
		
		project.getBuild().addResource(resource);
	}
	
	/**
	 * construct a Resource from a given path, including all children
	 * 
	 * @param path
	 * @return
	 */
	private Resource newResource(String path) {
		boolean isDirectory = new File(path).isDirectory();
		String directory =  isDirectory ? path : new File(path).getParent();
		String singleInclude = isDirectory ? "**/*.*" : new File(path).getName();
		
		Resource resource = new Resource();
		resource.setDirectory(directory);
		resource.addInclude(singleInclude);
		
		return resource;
	}
	
	/**
	 * add a source directory to the POM *if needed* (i.e. the src directory must not null) 
	 * 
	 * @param path
	 * @param pom
	 * @param sourceType
	 * @throws Exception
	 */
	public void addSource(String path, File pom, String sourceType) throws Exception {
		
		Project project = projectReader.read(pom);
		
		if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setAspectSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setUnitTestSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setIntegrationUnitTestSourceDirectory(path);
		}
		
		write(project, pom);
	}
	
	/** 
	 * add a Dependency identified by its absoluteFileName (artifact) to the POM. 
	 * checks first that the Dependency isnot already present is the dependencies list. 
	 * 
	 * @param absoluteFileName
	 * @param pom
	 * @throws Exception
	 */
	public void addDependency(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		
		Dependency dependency = DependencyUtil.getDependency(path);
		
		if ( !isDependencyPresent(project, dependency) ) {
			
			project.addDependency(dependency);
			write(project, pom);
			
		}
	}
	
	/**
	 * checks if a Dependency identified by its artifact path is present in the POM.
	 * default visibility for testing purpose
	 * 
	 * testing artifact doesnt seem to be a good solution since it is often omitted
	 * we rather have to test artifactId and version.
	 * 
	 * deeply depends upon a fully functionnal version of both methods :
	 * 
	 * org.mevenide.project.DependencyUtil#guessVersion() and
	 * org.mevenide.project.DependencyUtil#guessArtifactId()
	 * 
	 * @param project
	 * @param absoluteFileName
	 * @return
	 */
	boolean isDependencyPresent(Project project, Dependency dependency) {
		List dependencies = project.getDependencies();
		if ( dependencies == null ) {
			return false;
		}
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency declaredDependency = (Dependency) dependencies.get(i);
			
			String version = declaredDependency.getVersion(); 
			String artifactId = declaredDependency.getArtifactId();
			
			if (  artifactId != null && artifactId.equals(dependency.getArtifactId()) 
				  && version != null && version.equals(dependency.getVersion())) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * utility method that allows some factorization
	 * 
	 * @param project
	 * @param pom
	 * @throws IOException
	 * @throws Exception
	 */
	private void write(Project project, File pom) throws IOException, Exception {
		Writer writer = new FileWriter(pom, false);
		marshaller.marshall(writer, project);
		writer.close();
	}

}
