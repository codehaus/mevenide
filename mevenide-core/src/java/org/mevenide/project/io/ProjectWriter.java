/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.project.io;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.resource.DefaultResourceResolver;
import org.mevenide.project.resource.IResourceResolver;
import org.mevenide.project.resource.ResourceUtil;
import org.mevenide.project.source.SourceDirectoryUtil;

/**
 * 
 * @todo use a single instancce per POM
 * 
 * @doco each addition writes the pom :
 *     <code>write(project, pom);</code>
 * thus if the process fails it doesnt affect 
 * the previously added artifacts.
 * 
 * @todo transaction-awareness 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private static Log log = LogFactory.getLog(ProjectWriter.class);
	
	private static ProjectWriter projectWriter = null;
	
	private IResourceResolver resourceResolver = new DefaultResourceResolver();
	private ProjectReader projectReader ;
	private IProjectMarshaller marshaller ; 
	private JarOverrideWriter jarOverrideWriter = new JarOverrideWriter(this);
	
	private ProjectWriter() throws Exception  {
		marshaller = new DefaultProjectMarshaller();
		projectReader = ProjectReader.getReader();
	}
	
	public static synchronized ProjectWriter getWriter() throws Exception {
		if (projectWriter == null) {
			projectWriter = new ProjectWriter();
		}
		return projectWriter;
	}
	
	
	/**
	 * add a resource entry to the ${pom.build} 
	 * the resource is expected to be a directory, however we will 
	 * handle the case where it is a single file
	 * 
	 * @param path
	 * @param pom
	 */
	public void addResource(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		resourceResolver.mergeSimilarResources(project, resource);
		write(project, pom);	
	}
	
	public void addUnitTestResource(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		if ( project.getBuild().getUnitTest() == null ) {
			project.getBuild().setUnitTest(new UnitTest());
		}
		resourceResolver.mergeSimilarUnitTestResources(project, resource);
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
	
	public void setDependencies(List dependencies, File pom) throws Exception {
		setDependencies(dependencies, pom, true);
	}
	
	public void setDependencies(List dependencies, File pom, boolean shouldWriteProperties) throws Exception {
		Project project = projectReader.read(pom);
		List nonResolvedDependencies = DependencyUtil.getNonResolvedDependencies(dependencies);
		
		log.debug("writing " + dependencies.size() + " resolved dependencies, " + nonResolvedDependencies.size() + " non resolved ones");
		
		project.setDependencies(dependencies);
		write(project, pom);
		
		if ( shouldWriteProperties ) {
			overrideUnresolvedDependencies(pom, nonResolvedDependencies);
		}
		else {
			//@todo this is pretty cheap.. just a temp soltuion 
			dependencies.addAll(nonResolvedDependencies);
			project.setDependencies(dependencies);
			write(project, pom);
		}
	}

	private void overrideUnresolvedDependencies(File pom, List nonResolvedDependencies) throws IOException, Exception {
		File propertiesFile = new File(pom.getParent(), "project.properties");
		if ( !propertiesFile.exists() ) {
			propertiesFile.createNewFile();
		}
		//jarOverrideWriter.unsetOverriding(propertiesFile);
		
		//jaroverriding
		for (int i = 0; i < nonResolvedDependencies.size(); i++) {
			Dependency dependency = (Dependency) nonResolvedDependencies.get(i); 
			jarOverrideWriter.jarOverride(dependency, propertiesFile, pom);
		}
	}

	void write(Project project, File pom) throws Exception {
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
		
		Dependency dependency = reader.extractDependency(referencedPom);
		
		Project project = reader.read(pom);
		project.addDependency(dependency);
		
		write(project, pom);
	}

	public void resetSourceDirectories(File pomFile) throws Exception {
					
		ProjectReader reader = ProjectReader.getReader();
		Project project = reader.read(pomFile);
		if ( project.getBuild() != null ) {
			project.getBuild().setAspectSourceDirectory(null);
			project.getBuild().setIntegrationUnitTestSourceDirectory(null);
			
			project.getBuild().setUnitTestSourceDirectory(null);
			project.getBuild().setUnitTest(null);
			
			project.getBuild().setSourceDirectory(null);
			
			project.getBuild().setResources(new ArrayList());
//			if ( project.getBuild().getUnitTest() != null ) {
//				project.getBuild().getUnitTest().setResources(new ArrayList());
//			}
			
			marshaller.marshall(new FileWriter(pomFile), project);
	
		}
	}
	
	public void updateExtend(File pomFile, boolean isInherited, String parentPom) throws Exception {
		log.debug("isInherited = " + (isInherited) + " ; parentPom = " + parentPom);
		ProjectReader reader = ProjectReader.getReader();
		Project project = reader.read(pomFile);
		if ( !isInherited ) {
			project.setExtend(null);
		}
		else {
			project.setExtend(parentPom);
		}
		marshaller.marshall(new FileWriter(pomFile), project);
	}
	
	public void write(Project project) throws Exception {
		if ( project.getFile() != null ) {
			Writer writer = new FileWriter(project.getFile());
			
			marshaller.marshall(writer, project);
		}
	}
}
