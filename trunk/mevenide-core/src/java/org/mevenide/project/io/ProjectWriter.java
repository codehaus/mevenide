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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.apache.maven.repository.Artifact;
import org.mevenide.project.ProjectConstants;
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
	 * the resource is expected to be a directory
	 */
	public void addResource(String path, File pom, String[] exclusionPatterns) throws Exception {
		Project project = projectReader.read(pom);
		
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		
		Resource resource = ResourceUtil.newResource(path, exclusionPatterns);
		
		project.getBuild().getResources().add(resource);
		write(project, pom);	
	}
	
	public void addUnitTestResource(String path, File pom, String[] exclusionPatterns) throws Exception {
		Project project = projectReader.read(pom);
		
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		if ( project.getBuild().getUnitTest() == null ) {
			project.getBuild().setUnitTest(new UnitTest());
		}
		
		Resource resource = ResourceUtil.newResource(path, exclusionPatterns);
		project.getBuild().getUnitTest().getResources().add(resource);
		
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
	
	/**
	 * removes a directory : 
	 * may be one of sourceDirectory, unitTestSourceDirectory, aspectSourceDirectory, or resource or test resource directory
	 * it just dispatches to removeResource(Project, path) and removeSource(Project, path) 
	 * 
	 * @throws Exception if type different from either 
	 *    o MAVEN_SRC_DIRECTORY
	 *    o MAVEN_TEST_DIRECTORY
	 *    o MAVEN_ASPECT_DIRECTORY
	 *    o MAVEN_RESOURCE_DIRECTORY 
	 *    o MAVEN_TEST_RESOURCE_DIRECTORY
	 * @see #removeResource(Project, path)
	 * @see #removeSource(Project, path)
	 */
	public void removeDirectory(Project project, String path, String type) throws Exception {
	    if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(type) || ProjectConstants.MAVEN_TEST_DIRECTORY.equals(type) 
	            											   || ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(type) ) {
	        removeSource(project, path);
	    }
	    else if ( ProjectConstants.MAVEN_RESOURCE.equals(type) || ProjectConstants.MAVEN_TEST_RESOURCE.equals(type)) {
	        removeResource(project, path);
	    }
	    else {
	        throw new Exception("unhandled type : " + type);
	    }
	    write(project);
	}

    public void removeResource(Project project, String path) throws Exception {
        if ( project != null && project.getBuild() != null ) {
	        List resources = project.getBuild().getResources();
	        if ( resources != null && resources.size() > 0 ) {
	            removeResource(resources, path);
	            project.getBuild().setResources(resources);
	    	}
	        
	        if ( project.getBuild().getUnitTest() != null ) {
		        List unitTestResources = project.getBuild().getUnitTest().getResources();
		        if ( resources != null ) {
		            removeResource(unitTestResources, path);
		            project.getBuild().getUnitTest().setResources(unitTestResources);
		    	}
	        }
        }
    }

    private void removeResource(List resources, String path) {
        if ( resources != null ) {
	        Iterator sourceIterator = resources.iterator();
	        boolean changed = false;
	        while ( sourceIterator.hasNext() ) {
	            String iteratedResource = ((Resource) sourceIterator.next()).getDirectory();
	            if ( path.equals(iteratedResource) ) {
	                sourceIterator.remove();
	                changed = true;
	            }
	        }
        }
    }

    public void removeSource(Project project, String path) throws Exception {
       if ( project != null && project.getBuild() != null && path != null ) {
           if ( path.equals(project.getBuild().getSourceDirectory()) ) {
               project.getBuild().setSourceDirectory(null);
           }
           if ( path.equals(project.getBuild().getAspectSourceDirectory()) ) {
               project.getBuild().setAspectSourceDirectory(null);
           }
           if ( path.equals(project.getBuild().getUnitTestSourceDirectory()) ) {
               project.getBuild().setUnitTestSourceDirectory(null);
           }
       }
    }

    public void removeArtifact(Project project, Artifact artifact) throws Exception {
	    if ( project == null ) {
	        throw new Exception("project shouldnot be null");
	    } 
	    if ( artifact == null ) {
	        throw new Exception("artifact shouldnot be null");
	    }
	    
	    List projectDependencies = project.getDependencies();
		List iteratedList = new ArrayList(projectDependencies);
		int idx = 0;
		boolean warn = true;
		for (int i = 0; i < iteratedList.size(); i++) {
			Dependency iteratedDependency = (Dependency) iteratedList.get(i);
			if ( DependencyUtil.areEquals(project, artifact.getDependency(), iteratedDependency) ) {
			    project.getDependencies().remove(idx);
			    warn = false;
			}
			else {
				idx++;
			}
		}
		
		if ( warn ) {
		    log.warn("specified dependency not found");
		}
		else {
		    ProjectWriter.getWriter().write(project);
		}
	}
}
