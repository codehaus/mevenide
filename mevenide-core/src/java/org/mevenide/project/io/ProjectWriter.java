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
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.model.UnitTest;
import org.apache.maven.project.MavenProject;
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
 * @author Gilles Dodinet (rhill2@free.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private static Log log = LogFactory.getLog(ProjectWriter.class);
	
	private static ProjectWriter projectWriter = null;
	
	private IResourceResolver resourceResolver = new DefaultResourceResolver();
	private ProjectReader projectReader ;
	private DefaultProjectMarshaller marshaller ; 
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
		MavenProject project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getModel().getBuild() == null ) {
			project.getModel().setBuild(new Build());
		}
		resourceResolver.mergeSimilarResources(project, resource);
		write(project, pom);	
	}
	
	public void addUnitTestResource(String path, File pom) throws Exception {
		MavenProject project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getModel().getBuild() == null ) {
			project.getModel().setBuild(new Build());
		}
		if ( project.getModel().getBuild().getUnitTest() == null ) {
			project.getModel().getBuild().setUnitTest(new UnitTest());
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
		
		MavenProject project = projectReader.read(pom);
		
		if ( !SourceDirectoryUtil.isSourceDirectoryPresent(project, path)) {
			
			SourceDirectoryUtil.addSource(project, path, sourceType);
			write(project, pom);
			
		}
		
	}
	
	public void setDependencies(List dependencies, File pom) throws Exception {
		setDependencies(dependencies, pom, true);
	}
	
	public void setDependencies(List dependencies, File pom, boolean shouldWriteProperties) throws Exception {
		MavenProject project = projectReader.read(pom);
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

	void write(MavenProject project, File pom) throws Exception {
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
		
		MavenProject project = reader.read(pom);
		if ( !DependencyUtil.isDependencyPresent(project, dependency) ) {
			project.getModel().addDependency(dependency);
		}
		write(project, pom);
	}

	public void resetSourceDirectories(File pomFile) throws Exception {
					
		ProjectReader reader = ProjectReader.getReader();
		MavenProject project = reader.read(pomFile);
		if ( project.getModel().getBuild() != null ) {
			project.getModel().getBuild().setAspectSourceDirectory(null);
			
			project.getModel().getBuild().setUnitTestSourceDirectory(null);
			project.getModel().getBuild().setUnitTest(null);
			
			project.getModel().getBuild().setSourceDirectory(null);
			
			project.getBuild().setResources(new ArrayList());
//			if ( project.getBuild().getUnitTest() != null ) {
//				project.getModel().getBuild().getUnitTest().setResources(new ArrayList());
//			}
			
			IProjectMarshaller iProjectMarshaller = new DefaultProjectMarshaller();
			iProjectMarshaller.marshall(new FileWriter(pomFile), project);
	
		}
	}
	
	public void updateExtend(File pomFile, boolean isInherited, String parentPom) throws Exception {
		log.debug("isInherited = " + (isInherited) + " ; parentPom = " + parentPom);
		ProjectReader reader = ProjectReader.getReader();
		MavenProject project = reader.read(pomFile);
		if ( !isInherited ) {
			project.getModel().setExtend(null);
		}
		else {
			project.getModel().setExtend(parentPom);
		}
		IProjectMarshaller iProjectMarshaller = new DefaultProjectMarshaller();
		iProjectMarshaller.marshall(new FileWriter(pomFile), project);
	}
	
	public void write(MavenProject project) throws Exception {
		if ( project.getFile() != null ) {
			Writer writer = new FileWriter(project.getFile());
			
			marshaller.marshall(writer, project);
		}
	}
}
