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
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.project.dependency.AbstractDependencyResolver;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.mevenide.project.resource.DefaultResourceResolver;
import org.mevenide.project.resource.IResourceResolver;

/**
 * 
 * @todo use a single instancce per POM
 * 
 * each addition writes the pom :
 *     <code>write(project, pom);</code>
 * thus if the process fails it doesnt affect 
 * the previously added artifacts. that is obviously not optimized, tho performance 
 * isnot a issue here. 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private ProjectReader projectReader ;
	private DefaultProjectMarshaller marshaller ; 
	private IDependencyResolver dependencyResolver;
	private IResourceResolver resourceResolver;
	
	private static ProjectWriter projectWriter = null;
	private static Object lock = new Object();
	
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
	
	private ProjectWriter() throws Exception  {
		marshaller = new DefaultProjectMarshaller();
		projectReader = ProjectReader.getReader();
		dependencyResolver = AbstractDependencyResolver.getInstance();
		resourceResolver = new DefaultResourceResolver();
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
		Resource resource = resourceResolver.newResource(path);
		resourceResolver.mergeSimilarResources(project, resource);
		write(project, pom);	
	}
	
	/**
	 * add a source directory to the POM *if needed* 
     * 
     * @pre the src directory must not null 
	 * 
	 * @param path
	 * @param pom
	 * @param sourceType
	 * @throws Exception
	 */
	public void addSource(String path, File pom, String sourceType) throws Exception {
		
		Project project = projectReader.read(pom);
		
		if ( !SourceDirectoryUtil.isSourceDirectoryPresent(project, path)) {
			
			SourceDirectoryUtil.addSource(project, path, sourceType);
			write(project, pom);
			
		}
		
	}
	
	/** 
	 * add a Dependency identified by its absoluteFileName (artifact) to the POM. 
	 * Try to resolve the dependency using the dependency factory 
	 * checks first that the Dependency isnot already present is the dependencies list. 
	 * 
	 * @param absoluteFileName
	 * @param pom
	 * @throws Exception
	 */
	public void addDependency(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		
		Dependency dependency = DependencyFactory.getFactory().getDependency(path);
		
		if ( !dependencyResolver.isDependencyPresent(project, dependency) ) {
			
			project.addDependency(dependency);
			write(project, pom);
			
		}
		
		//@todo manage jar.overridding
		
	}
	
	
	public void addDependency(Dependency dependency, File pom) throws Exception {
		Project project = projectReader.read(pom);

		if ( !dependencyResolver.isDependencyPresent(project, dependency) ) {
			
			project.addDependency(dependency);
			write(project, pom);
	
		}
		
		//@todo jaroverriding 
	}
	
	public void setDependencies(List dependencies, File pom) throws Exception {
		Project project = projectReader.read(pom);

		project.setDependencies(dependencies);
		write(project, pom);
	
		//@todo jaroverriding 
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
	
	
	/**
	 * add a project dependency to this POM, reading required information from the referenced POM
	 * 
	 * @param referencedPom
	 * @param pom
	 * @throws Exception
	 */
	public void addProject(File referencedPom, File pom) throws Exception {
		ProjectReader reader = ProjectReader.getReader();
		
		Dependency dependency = reader.getDependency(referencedPom);
		
		Project project = reader.read(pom);
		project.addDependency(dependency);
		
		write(project, pom);
	}

	

}
